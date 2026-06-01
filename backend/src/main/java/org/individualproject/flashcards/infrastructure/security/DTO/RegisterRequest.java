package org.individualproject.flashcards.infrastructure.security.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        String username,
        @NotBlank
        String email,
        @NotBlank
        @Min(8)
        String password
) {
}
