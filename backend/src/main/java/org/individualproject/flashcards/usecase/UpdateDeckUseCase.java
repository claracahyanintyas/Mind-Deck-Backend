package org.individualproject.flashcards.usecase;

import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;

public interface UpdateDeckUseCase {
    DeckPublicData  updateDeck(Long id, UpdateDeckRequest deck);
}
