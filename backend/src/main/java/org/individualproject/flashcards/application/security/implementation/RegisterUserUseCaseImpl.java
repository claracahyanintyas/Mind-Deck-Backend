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
import org.individualproject.flashcards.security.CustomUserDetails;
import org.individualproject.flashcards.security.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
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
            throw new IllegalArgumentException();
        }
        if (userRepository.existsByEmail(registerCommand.email())){
            throw new EmailTakenException();
        }
        if (!registerCommand.username().equals(guestUsername) && userRepository.existsByUsername(registerCommand.username())) {
            throw new UsernameTakenException();
        }
        var guestUser = userRepository.findByUsernameOrEmail(guestUsername, guestUsername).orElseThrow(UserNotFoundException::new);
        guestUser.setUserCredentials(registerCommand.username(), registerCommand.email(), passwordEncoder.encode(registerCommand.password()));
        var userRole = roleRepository.findByName("ROLE_USER").orElseThrow(RoleNotFoundException::new);
        guestUser.addRole(userRole);
        guestUser.removeRole("ROLE_GUEST");
        var savedUser = userRepository.save(guestUser);

        // 2. FIX: Map authorities directly from the saved user roles
        Set<GrantedAuthority> authorities = savedUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // 3. FIX: Build CustomUserDetails directly from savedUser properties
        CustomUserDetails userDetails = new CustomUserDetails(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getPassword(),
                authorities
        );

        // 4. FIX: Create the security token context without querying the DB again
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        // 5. FIX: Pass the authenticated token context object to generate token string
        String token = jwtTokenProvider.generateToken(authentication);

        UserPublicData userPublicData =new UserPublicData(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getCreatedAt(),
                savedUser.isActive(), savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new jwtAuthOutput(token, userPublicData);
    }
}
