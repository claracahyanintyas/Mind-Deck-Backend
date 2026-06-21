package org.individualproject.flashcards.application.security.implementation;

import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private RefreshTokenUseCaseImpl refreshTokenUseCaseImpl;

    @Test
    void rotateSessionTokens_WithValidToken_ShouldRotateAndReturnNewTokens() {
        // Arrange
        String oldRefreshToken = "old-valid-uuid-refresh-token";
        String expectedAccessToken = "new-rotated-jwt-access-token";
        long mockRefreshExpirationMs = 3600000L;

        // Instantiate a real domain user instance
        User user = new User(1L, "username", "email@test.com", "pass", OffsetDateTime.now(), true, new HashSet<>(), oldRefreshToken, Instant.now().plusSeconds(mockRefreshExpirationMs));

        when(userRepository.findByRefreshToken(oldRefreshToken)).thenReturn(Optional.of(user));
        when(tokenProvider.generateTokenFromUsername(user.getUsername())).thenReturn(expectedAccessToken);
        when(tokenProvider.getRefreshExpirationMs()).thenReturn(mockRefreshExpirationMs);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        jwtAuthOutput result = refreshTokenUseCaseImpl.rotateSessionTokens(oldRefreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(expectedAccessToken, result.token());
        assertNotNull(result.refreshToken());
        assertNotEquals(oldRefreshToken, result.refreshToken(), "Refresh token should be completely regenerated");
        assertEquals("username", result.user().username());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void rotateSessionTokens_WithExpiredToken_ShouldClearTokenAndThrowRuntimeException() {
        // Arrange
        String expiredToken = "expired-uuid-token";

        // Instantiate an expired session profile using an expiry window pinned in the past
        User expiredUser = new User(1L, "username", "email@test.com", "pass", OffsetDateTime.now(), true, new HashSet<>(), expiredToken, Instant.now().minus(89837231L, ChronoUnit.MILLIS));

        when(userRepository.findByRefreshToken(expiredToken)).thenReturn(Optional.of(expiredUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                refreshTokenUseCaseImpl.rotateSessionTokens(expiredToken)
        );
        assertTrue(exception.getMessage().contains("Refresh token expired"));

        // Verify domain cleaning logic side effects were properly persisted to DB
        assertNull(expiredUser.getRefreshToken());
        verify(userRepository, times(1)).save(expiredUser);
        verify(tokenProvider, never()).generateTokenFromUsername(anyString());
    }

    @Test
    void rotateSessionTokens_TokenNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        String invalidToken = "fake-token";
        when(userRepository.findByRefreshToken(invalidToken)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                refreshTokenUseCaseImpl.rotateSessionTokens(invalidToken)
        );
        verify(userRepository, never()).save(any());
        verifyNoInteractions(tokenProvider);
    }
}