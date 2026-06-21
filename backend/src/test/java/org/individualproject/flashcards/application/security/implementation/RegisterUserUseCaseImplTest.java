package org.individualproject.flashcards.application.security.implementation;

import org.individualproject.flashcards.application.exception.EmailTakenException;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.exception.UsernameTakenException;
import org.individualproject.flashcards.application.persistence.RoleRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.RegisterCommand;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RegisterUserUseCaseImpl registerUserUseCaseImpl;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUser_StandardFlow_ShouldCreateNewUserAndReturnTokens() {
        // Arrange
        var command = new RegisterCommand("newUser", "new@test.com", "password123");
        Role userRole = Role.builder().id(1L).name("ROLE_USER").build();
        String mockToken = "standard-jwt-token";

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(userRepository.existsByUsername(command.username())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(command.password())).thenReturn("encodedPassword");
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(3600000L);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(mockToken);

        // Mock save using lambda response to preserve generated domain instance fields
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        jwtAuthOutput result = registerUserUseCaseImpl.registerUser(command, null);

        // Assert
        assertNotNull(result);
        assertEquals(mockToken, result.token());
        assertNotNull(result.refreshToken());
        assertEquals(command.username(), result.user().username());
        assertTrue(result.user().roles().contains("ROLE_USER"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_GuestUpgradeFlow_ShouldMutateExistingGuestSession() {
        // Arrange
        String guestUsername = "guest_12345678";
        var command = new RegisterCommand(guestUsername, "upgrade@test.com", "securePass");

        Role userRole = Role.builder().id(1L).name("ROLE_USER").build();
        Role guestRole = Role.builder().id(2L).name("ROLE_GUEST").build();

        // Setup initial user state matching an active guest profile
        User existingGuest = new User(10L, guestUsername, null, "", OffsetDateTime.now(), true, new HashSet<>(), null, null);
        existingGuest.addRole(guestRole);

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.findByUsername(guestUsername)).thenReturn(Optional.of(existingGuest));
        when(passwordEncoder.encode(command.password())).thenReturn("encodedPass");
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(3600000L);
        when(userRepository.save(existingGuest)).thenReturn(existingGuest);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("upgraded-token");

        // Act
        jwtAuthOutput result = registerUserUseCaseImpl.registerUser(command, guestUsername);

        // Assert
        assertNotNull(result);
        assertEquals("upgraded-token", result.token());
        assertEquals(command.email(), result.user().email());
        assertTrue(result.user().roles().contains("ROLE_USER"));
        assertFalse(result.user().roles().contains("ROLE_GUEST"), "Guest role must be stripped out");

        verify(userRepository, times(1)).save(existingGuest);
    }

    @Test
    void registerUser_EmailAlreadyTaken_ShouldThrowEmailTakenException() {
        // Arrange
        var command = new RegisterCommand("user", "taken@test.com", "pass");
        when(userRepository.existsByEmail(command.email())).thenReturn(true);

        // Act & Assert
        assertThrows(EmailTakenException.class, () -> registerUserUseCaseImpl.registerUser(command, null));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_UsernameAlreadyTaken_ShouldThrowUsernameTakenException() {
        // Arrange
        var command = new RegisterCommand("takenUser", "clean@test.com", "pass");
        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(userRepository.existsByUsername(command.username())).thenReturn(true);

        // Act & Assert
        assertThrows(UsernameTakenException.class, () -> registerUserUseCaseImpl.registerUser(command, null));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_GuestSessionLostDuringUpgrade_ShouldThrowUserNotFoundException() {
        // Arrange
        String guestUsername = "missing_guest";
        var command = new RegisterCommand(guestUsername, "clean@test.com", "pass");
        Role userRole = Role.builder().id(1L).name("ROLE_USER").build();

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.findByUsername(guestUsername)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> registerUserUseCaseImpl.registerUser(command, guestUsername));
        verify(userRepository, never()).save(any());
    }
}