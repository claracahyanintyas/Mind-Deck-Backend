package org.individualproject.flashcards.application.security.implementation;

import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.LoginInput;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @AfterEach
    void tearDown() {
        // Clean up Spring Security context after each test execution to prevent cross-contamination
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WithValidCredentials_ShouldAuthenticateAndReturnJwtAuthOutput() {
        // Arrange
        var loginInput = new LoginInput("testUser", "password123");

        Authentication mockAuthentication = mock(Authentication.class);
        String expectedAccessToken = "mock-jwt-access-token";
        long mockRefreshExpirationMs = 604800000L; // 7 days

        // Instantiate a real domain user aggregate
        User user = new User(1L, "testUser", "test@example.com", "hashedPassword", OffsetDateTime.now(), true, new HashSet<>(), null, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtTokenProvider.generateToken(mockAuthentication)).thenReturn(expectedAccessToken);
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(mockRefreshExpirationMs);

        when(userRepository.findByUsernameOrEmail(loginInput.usernameOrEmail(), loginInput.usernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        jwtAuthOutput result = authServiceImpl.login(loginInput);

        // Assert
        assertNotNull(result);
        assertEquals(expectedAccessToken, result.token()); // Or result.getToken() depending on your DTO record property names
        assertNotNull(result.refreshToken());
        assertNotNull(result.user());
        assertEquals("testUser", result.user().username());

        // Verify the user was safely tied to the active security request thread context
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(currentAuth);
        assertEquals(mockAuthentication, currentAuth);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void login_UserNotFoundAfterAuthentication_ShouldThrowUserNotFoundException() {
        // Arrange
        var loginInput = new LoginInput("ghostUser", "password123");
        Authentication mockAuthentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtTokenProvider.generateToken(mockAuthentication)).thenReturn("some-token");

        when(userRepository.findByUsernameOrEmail(loginInput.usernameOrEmail(), loginInput.usernameOrEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authServiceImpl.login(loginInput));

        // Assert that state modifications and security context bindings never occurred
        verify(userRepository, never()).save(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}