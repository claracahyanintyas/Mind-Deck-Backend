package org.individualproject.flashcards.domain.user;

import lombok.Builder;
import lombok.Getter;
import org.individualproject.flashcards.domain.role.Role;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter @Builder
public class User {
    Long id;
    String username;
    String email;
    String password;
    @Builder.Default
    private OffsetDateTime createdAt =  OffsetDateTime.now();
    private Set<Role> roles;
}
