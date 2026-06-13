package org.individualproject.flashcards.application.deck;

import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.deck.DTO.UpdateDeckCommand;

public interface UpdateDeckUseCase {
    DeckPublicData  updateDeck(Long id, UpdateDeckCommand command, String username);
}
