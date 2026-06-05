package org.individualproject.flashcards.infrastructure.security.DTO;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String usernameOrEmail,
        @NotBlank
        String password) {
}
