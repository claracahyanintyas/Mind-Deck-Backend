package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.UserJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository @RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private UserJpaRepository userJpaRepository;
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userJpaRepository.findByUsernameOrEmail(username, email).map(UserEntityMapper::fromEntity);
    }
}
