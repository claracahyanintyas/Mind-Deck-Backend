package org.individualproject.flashcards.infrastructure.deck.DTO;

import jakarta.validation.constraints.*;

public record UpdateDeckRequest (
        @NotBlank(message = "name cannot be blank")
        @NotNull
        @Size(min = 1, max = 100, message = "name can only be 1-100 characters long")
        String name,
        @Size(max = 400, message = "description cannot be more than 400 characters")
        String description,
        @NotNull
        Boolean isPrivate
)
{}