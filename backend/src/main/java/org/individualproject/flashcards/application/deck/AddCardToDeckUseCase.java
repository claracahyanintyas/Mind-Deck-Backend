package org.individualproject.flashcards.application.deck;

import org.individualproject.flashcards.application.card.DTO.AddCardCommand;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;

public interface AddCardToDeckUseCase {
    CardPublicData addCardToDeck(AddCardCommand command);
}
