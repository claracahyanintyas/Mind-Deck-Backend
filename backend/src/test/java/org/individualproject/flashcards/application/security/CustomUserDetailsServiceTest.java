package org.individualproject.flashcards.application.security;

import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_WithValidUsernameOrEmail_ShouldReturnCustomUserDetails() {
        // Arrange
        String identifier = "alex_dev";

        Role mockRole = mock(Role.class);
        when(mockRole.getName()).thenReturn("ROLE_STUDENT");

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(500L);
        when(mockUser.getUsername()).thenReturn("alex_dev");
        when(mockUser.getPassword()).thenReturn("encoded_password_hash");
        when(mockUser.getRoles()).thenReturn(Set.of(mockRole));

        // Stubbing the dual-lookup method used by the lookup pipeline
        when(userRepository.findByUsernameOrEmail(identifier, identifier))
                .thenReturn(Optional.of(mockUser));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername(identifier);

        // Assert
        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);

        CustomUserDetails customResult = (CustomUserDetails) result;
        assertEquals(500L, customResult.getId());
        assertEquals("alex_dev", customResult.getUsername());
        assertEquals("encoded_password_hash", customResult.getPassword());

        // Map granted authorities to strings to check content safely
        Set<String> authorityStrings = customResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertTrue(authorityStrings.contains("ROLE_STUDENT"));
        assertEquals(1, authorityStrings.size());

        verify(userRepository, times(1)).findByUsernameOrEmail(identifier, identifier);
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String nonExistentUser = "missing_user@test.com";
        when(userRepository.findByUsernameOrEmail(nonExistentUser, nonExistentUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(nonExistentUser)
        );

        assertEquals("User not exists by Username or Email", exception.getMessage());
        verify(userRepository, times(1)).findByUsernameOrEmail(nonExistentUser, nonExistentUser);
    }
}