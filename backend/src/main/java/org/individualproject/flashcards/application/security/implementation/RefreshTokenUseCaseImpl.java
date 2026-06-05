package org.individualproject.flashcards.application.security.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.RefreshTokenUseCase;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public jwtAuthOutput rotateSessionTokens(String refreshToken) {
        // 1. Locate the active session record via token value
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UserNotFoundException("Session invalid or already logged out."));

        // 2. Delegate logic to the Domain Model rules
        if (user.isRefreshTokenExpired()) {
            user.clearRefreshToken();
            userRepository.save(user);
            throw new RuntimeException("Refresh token expired. Please re-authenticate.");
        }

        // 3. Issue rotated safe identifiers
        String newAccessToken = tokenProvider.generateTokenFromUsername(user.getUsername());
        String newRefreshToken = UUID.randomUUID().toString();

        // 4. Mutate and save domain state
        user.updateRefreshToken(newRefreshToken, tokenProvider.getRefreshExpirationMs());
        User savedUser = userRepository.save(user);

        UserPublicData userPublicData = new UserPublicData(
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getCreatedAt(),
                savedUser.isActive(), savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        return new jwtAuthOutput(newAccessToken, newRefreshToken, userPublicData);
    }
}