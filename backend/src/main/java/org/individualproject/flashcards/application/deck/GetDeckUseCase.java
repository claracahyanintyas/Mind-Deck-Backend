package org.individualproject.flashcards.application.deck;

import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;

public interface GetDeckUseCase {
    DeckPublicData getDeck(Long id);
}
