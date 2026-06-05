package org.individualproject.flashcards.application.user.implementation;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.application.user.GetUserByUsernameUseCase;
import org.individualproject.flashcards.domain.role.Role;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class GetUserByUsernameUseCaseImpl implements GetUserByUsernameUseCase {
    private final UserRepository userRepository;

    @Override
    public UserPublicData getUser(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is null or empty");
        }
        var user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return new UserPublicData(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt(),
                user.isActive(), user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }
}
