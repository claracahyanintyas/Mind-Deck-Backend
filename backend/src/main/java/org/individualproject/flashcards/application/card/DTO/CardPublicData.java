package org.individualproject.flashcards.application.card.DTO;

import org.individualproject.flashcards.domain.card.ContentType;

import java.time.OffsetDateTime;

public record CardPublicData(
        Long id,
        String frontContent,
        ContentType frontContentType,
        String backContent,
        ContentType backContentType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
