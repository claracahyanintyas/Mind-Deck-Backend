package org.individualproject.flashcards.infrastructure.config.database.mapper;

import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.config.database.entity.UserEntity;

import java.util.HashSet;
import java.util.stream.Collectors;

public class UserEntityMapper {
    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .passwordHash(user.getPassword())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles() == null ? new HashSet<>() :
                        user.getRoles().stream().map(RoleEntityMapper::toEntity).collect(Collectors.toSet()))
                .build();
    }

    public static User fromEntity(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPasswordHash())
                .createdAt(userEntity.getCreatedAt())
                .roles(userEntity.getRoles() == null ? new HashSet<>() :
                        userEntity.getRoles().stream().map(RoleEntityMapper::fromEntity).collect(Collectors.toSet()))
                .build();
    }
}
