package org.individualproject.flashcards.infrastructure.deck.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDeckRequest (
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 100)
    String name,
    @Size(max = 400)
    String description,
    @NotNull
    Boolean isPrivate
)
    {}
