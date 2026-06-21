package org.individualproject.flashcards.application.security.implementation;

import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.RoleRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceImplTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GuestServiceImpl guestServiceImpl;

    @AfterEach
    void tearDown() {
        // Clear Spring Security Context to prevent thread leaks across tests
        SecurityContextHolder.clearContext();
    }

    @Test
    void createGuestSession_ShouldCreateGuestUserAndReturnTokens() {
        // Arrange
        Role guestRole = Role.builder().name("ROLE_GUEST").id(1L).build();
        String expectedAccessToken = "mock-guest-jwt-token";
        long mockRefreshExpirationMs = 3600000L;

        when(roleRepository.findByName("ROLE_GUEST")).thenReturn(Optional.of(guestRole));
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(mockRefreshExpirationMs);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(expectedAccessToken);

        // Mock save to return whatever user instance gets generated inside the service
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        jwtGuestOutput result = guestServiceImpl.createGuestSession();

        // Assert
        assertNotNull(result);
        assertEquals(expectedAccessToken, result.token());
        assertNotNull(result.refreshToken());
        assertNotNull(result.user());
        assertTrue(result.user().username().startsWith("guest_"));
        assertTrue(result.user().roles().contains("ROLE_GUEST"));

        // Verify Authentication context injection
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createGuestSession_RoleMissing_ShouldThrowRuntimeException() {
        // Arrange
        when(roleRepository.findByName("ROLE_GUEST")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> guestServiceImpl.createGuestSession());
        verifyNoInteractions(userRepository, jwtTokenProvider);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void refreshExistingGuestSession_WithValidUsername_ShouldRotateTokens() {
        // Arrange
        String username = "guest_abc12345";
        Role guestRole = Role.builder().id(1L).name("ROLE_GUEST").build();

        User existingGuest = new User(10L, username, null, "", OffsetDateTime.now(), true, new HashSet<>(), null, null);
        existingGuest.addRole(guestRole);

        String secondaryAccessToken = "new-rotated-jwt-token";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingGuest));
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(3600000L);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(secondaryAccessToken);
        when(userRepository.save(existingGuest)).thenReturn(existingGuest);

        // Act
        jwtGuestOutput result = guestServiceImpl.refreshExistingGuestSession(username);

        // Assert
        assertNotNull(result);
        assertEquals(secondaryAccessToken, result.token());
        assertNotNull(result.refreshToken());
        assertEquals(username, result.user().username());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);

        verify(userRepository, times(1)).save(existingGuest);
    }

    @Test
    void refreshExistingGuestSession_GuestNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                guestServiceImpl.refreshExistingGuestSession("ghost_guest")
        );
        verify(userRepository, never()).save(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}