package org.individualproject.flashcards.usecase;

import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;

public interface GetDeckUseCase {
    DeckPublicData getDeck(Long id);
}
