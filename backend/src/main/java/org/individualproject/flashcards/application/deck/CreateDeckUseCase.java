package org.individualproject.flashcards.application.deck;

import org.individualproject.flashcards.application.deck.DTO.CreateDeckCommand;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;

public interface CreateDeckUseCase {
    DeckPublicData createDeck(CreateDeckCommand command);
}
