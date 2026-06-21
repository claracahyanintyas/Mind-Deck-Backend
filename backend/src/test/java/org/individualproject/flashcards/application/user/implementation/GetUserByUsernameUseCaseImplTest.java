package org.individualproject.flashcards.application.user.implementation;

import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User; // Assuming this package path
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByUsernameUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserByUsernameUseCaseImpl getUserByUsernameUseCase;

    @Test
    void getUser_WithValidUsername_ShouldReturnUserPublicData() {
        // Arrange
        String targetUsername = "john_doe";

        // Mocking the Roles domain entities
        Role userRole = mock(Role.class);
        when(userRole.getName()).thenReturn("ROLE_USER");

        // Mocking the main User entity layer
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn(targetUsername);
        when(mockUser.getEmail()).thenReturn("john@example.com");
        when(mockUser.getCreatedAt()).thenReturn(OffsetDateTime.now());
        when(mockUser.isActive()).thenReturn(true);
        when(mockUser.getRoles()).thenReturn(Set.of(userRole));

        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(mockUser));

        // Act
        UserPublicData result = getUserByUsernameUseCase.getUser(targetUsername);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(targetUsername, result.username());
        assertEquals("john@example.com", result.email());
        assertTrue(result.active());
        assertTrue(result.roles().contains("ROLE_USER"));
        assertEquals(1, result.roles().size());

        verify(userRepository, times(1)).findByUsername(targetUsername);
    }

    @Test
    void getUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        // Arrange
        String nonExistentUser = "ghost_user";
        when(userRepository.findByUsername(nonExistentUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                getUserByUsernameUseCase.getUser(nonExistentUser)
        );

        verify(userRepository, times(1)).findByUsername(nonExistentUser);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getUser_WhenUsernameIsNullOrEmpty_ShouldThrowIllegalArgumentException(String invalidUsername) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                getUserByUsernameUseCase.getUser(invalidUsername)
        );

        assertEquals("Username is null or empty", exception.getMessage());

        // Verify repository was never touched
        verifyNoInteractions(userRepository);
    }
}