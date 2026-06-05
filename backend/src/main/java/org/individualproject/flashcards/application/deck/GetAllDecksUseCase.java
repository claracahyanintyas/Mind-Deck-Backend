package org.individualproject.flashcards.application.deck;

import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;

import java.util.Collection;

public interface GetAllDecksUseCase {
    Collection<DeckPublicData> getAllDecks();
}
