package org.individualproject.flashcards.application.user.mapper;

import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;

import java.util.stream.Collectors;

public class UserDTOMapper {
    public static UserPublicData toDTO(User user) {
        return new UserPublicData(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt(),
                user.isActive(), user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }
}
