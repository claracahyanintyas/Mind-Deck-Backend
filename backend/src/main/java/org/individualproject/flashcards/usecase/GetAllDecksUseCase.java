package org.individualproject.flashcards.usecase;

import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;

import java.util.Collection;

public interface GetAllDecksUseCase {
    Collection<DeckPublicData> getAllDecks();
}
