package org.individualproject.flashcards.infrastructure.deck.DTO;

import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

public record DeckPublicData(Long id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt, Boolean isPrivate) {

}
