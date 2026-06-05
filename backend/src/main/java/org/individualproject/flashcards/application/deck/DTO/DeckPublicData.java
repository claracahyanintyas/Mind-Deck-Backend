package org.individualproject.flashcards.application.deck.DTO;

import org.individualproject.flashcards.application.card.DTO.CardPublicData;

import java.time.OffsetDateTime;
import java.util.List;

public record DeckPublicData(Long id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt, Boolean isPrivate, List<CardPublicData> cards) {

}
