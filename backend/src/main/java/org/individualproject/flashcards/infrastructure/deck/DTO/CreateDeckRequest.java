package org.individualproject.flashcards.infrastructure.deck.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDeckRequest (
    @NotBlank(message = "name cannot be blank")
    @Size(min = 1, max = 100)
    String name,
    @Size(max = 400, message = "description is too long")
    String description,
    @NotNull
    Boolean isPrivate
)
{}
