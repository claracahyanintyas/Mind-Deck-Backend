package org.individualproject.flashcards.application.deck.DTO;

import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;

import java.time.OffsetDateTime;
import java.util.List;

public record DeckPublicData(Long id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt, Boolean isPrivate, UserPublicData createdBy, List<CardPublicData> cards) {

}
