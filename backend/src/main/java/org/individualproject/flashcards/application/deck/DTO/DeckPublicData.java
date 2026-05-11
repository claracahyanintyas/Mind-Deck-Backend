package org.individualproject.flashcards.application.deck.DTO;

import java.time.OffsetDateTime;

public record DeckPublicData(Long id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt, Boolean isPrivate) {

}
