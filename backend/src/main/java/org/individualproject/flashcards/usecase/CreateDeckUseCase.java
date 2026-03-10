package org.individualproject.flashcards.usecase;

import org.individualproject.flashcards.domain.Deck;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;

public interface CreateDeckUseCase {
    DeckPublicData createDeck(CreateDeckRequest request);
}
