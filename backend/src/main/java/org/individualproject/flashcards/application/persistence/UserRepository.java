package org.individualproject.flashcards.application.persistence;

import org.individualproject.flashcards.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<User> findByUsernameOrEmail(String username, String email);
}
