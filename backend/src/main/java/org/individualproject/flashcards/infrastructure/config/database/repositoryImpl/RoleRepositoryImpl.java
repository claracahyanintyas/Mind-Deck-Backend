package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.RoleRepository;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.RoleJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.mapper.RoleEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    private RoleJpaRepository roleJpaRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(RoleEntityMapper::fromEntity);
    }
}
