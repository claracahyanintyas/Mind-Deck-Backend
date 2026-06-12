package org.individualproject.flashcards.application.card.DTO;

import org.individualproject.flashcards.domain.card.ContentType;

public record EditCardCommand(
        String frontContent,
        ContentType frontContentType,
        String backContent,
        ContentType backContentType
) {
}
