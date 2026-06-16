package org.individualproject.flashcards.infrastructure.security.DTO;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username or email cannot be blank")
        String usernameOrEmail,
        @NotBlank(message = "password cannot be blank")
        String password) {
}
