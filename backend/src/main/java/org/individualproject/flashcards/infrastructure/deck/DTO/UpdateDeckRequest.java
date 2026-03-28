package org.individualproject.flashcards.infrastructure.deck.DTO;

import jakarta.validation.constraints.*;

public record UpdateDeckRequest (
        @NotBlank
        @NotNull
        @Size(min = 1, max = 100)
        String name,
        @Size(max = 400)
        String description,
        @NotNull
        Boolean isPrivate
)
{}