package org.individualproject.flashcards.application.user.DTO;

import java.time.OffsetDateTime;
import java.util.Set;

public record UserPublicData(Long id, String username, String email, OffsetDateTime createdAt, boolean active, Set<String> roles) {
}
