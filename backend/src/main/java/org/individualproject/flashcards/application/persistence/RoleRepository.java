package org.individualproject.flashcards.application.persistence;

import org.individualproject.flashcards.domain.role.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
}
