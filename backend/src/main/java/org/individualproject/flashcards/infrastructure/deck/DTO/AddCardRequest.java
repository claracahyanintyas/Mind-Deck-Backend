package org.individualproject.flashcards.infrastructure.deck.DTO;

import jakarta.validation.constraints.NotBlank;
import org.individualproject.flashcards.domain.card.ContentType;

public record AddCardRequest(
        @NotBlank
        String frontContent,
        ContentType frontContentType,
        @NotBlank
        String backContent,
        ContentType backContentType
) {
}
