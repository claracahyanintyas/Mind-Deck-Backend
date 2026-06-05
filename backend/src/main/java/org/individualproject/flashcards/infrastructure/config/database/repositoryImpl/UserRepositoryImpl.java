package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.UserJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository @AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private UserJpaRepository userJpaRepository;
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userJpaRepository.findByUsernameOrEmail(username, email).map(UserEntityMapper::fromEntity);
    }
    public User save(User user) {
        return UserEntityMapper.fromEntity(userJpaRepository.save(UserEntityMapper.toEntity(user)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(UserEntityMapper::fromEntity);
    }
    @Override
    public Optional<User> findByRefreshToken(String refreshToken) {
        return userJpaRepository.findByRefreshToken(refreshToken).map(UserEntityMapper::fromEntity);
    }
}
