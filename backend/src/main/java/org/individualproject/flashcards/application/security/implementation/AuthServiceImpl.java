package org.individualproject.flashcards.application.security.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.AuthService;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.DTO.LoginInput;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service @AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;

    @Override
    public jwtAuthOutput login(LoginInput loginInput) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginInput.usernameOrEmail(),
                loginInput.password()
        ));

        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByUsernameOrEmail(loginInput.usernameOrEmail(), loginInput.usernameOrEmail())
                .orElseThrow(UserNotFoundException::new);
        UserPublicData userPublicData =new UserPublicData(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt(),
                user.isActive(), user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        jwtAuthOutput jwtAuthOutput = new jwtAuthOutput(token, userPublicData);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtAuthOutput;
    }
}
