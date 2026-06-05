package org.individualproject.flashcards.infrastructure.config.database.mapper;

import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.infrastructure.config.database.entity.RoleEntity;

public class RoleEntityMapper {
    public static RoleEntity toEntity(Role role) {
        return RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
    public static Role fromEntity(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
