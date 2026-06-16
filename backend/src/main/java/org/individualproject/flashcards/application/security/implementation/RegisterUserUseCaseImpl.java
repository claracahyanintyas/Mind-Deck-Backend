package org.individualproject.flashcards.application.security.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.exception.EmailTakenException;
import org.individualproject.flashcards.application.exception.RoleNotFoundException;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.exception.UsernameTakenException;
import org.individualproject.flashcards.application.persistence.RoleRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.RegisterCommand;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.RegisterUserUseCase;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.security.CustomUserDetails;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @AllArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    JwtTokenProvider jwtTokenProvider;
    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public jwtAuthOutput registerUser(RegisterCommand registerCommand, String guestUsername) {
        if (registerCommand == null) {
            throw new IllegalArgumentException("Registration command cannot be null");
        }

        if (userRepository.existsByEmail(registerCommand.email())) {
            throw new EmailTakenException();
        }

        boolean isGuestUpgrade = guestUsername != null && !guestUsername.isBlank();
        if (!isGuestUpgrade || !registerCommand.username().equals(guestUsername)) {
            if (userRepository.existsByUsername(registerCommand.username())) {
                throw new UsernameTakenException();
            }
        }

        User targetUser;
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(RoleNotFoundException::new);

        if (isGuestUpgrade) {
            targetUser = userRepository.findByUsername(guestUsername)
                    .orElseThrow(() -> new UserNotFoundException("Active guest session not found"));

            // Mutate the guest aggregate into a registered user template
            targetUser.setUserCredentials(
                    registerCommand.username(),
                    registerCommand.email(),
                    passwordEncoder.encode(registerCommand.password())
            );
            targetUser.addRole(userRole);
            targetUser.removeRole("ROLE_GUEST"); // Strip anonymous permissions
        } else {
            targetUser = new User(
                    registerCommand.username(),
                    registerCommand.email(),
                    passwordEncoder.encode(registerCommand.password())
            );
            targetUser.addRole(userRole);
        }

        String refreshToken = UUID.randomUUID().toString();
        targetUser.updateRefreshToken(refreshToken, jwtTokenProvider.getRefreshExpirationMs());

        var savedUser = userRepository.save(targetUser);

        Set<GrantedAuthority> authorities = savedUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        CustomUserDetails userDetails = new CustomUserDetails(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getPassword(),
                authorities
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        String accessToken = jwtTokenProvider.generateToken(authentication);

        UserPublicData userPublicData = new UserPublicData(
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getCreatedAt(),
                savedUser.isActive(), savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new jwtAuthOutput(accessToken, refreshToken, userPublicData);
    }
}
