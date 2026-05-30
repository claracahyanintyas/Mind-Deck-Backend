package org.individualproject.flashcards.application.card;

import org.individualproject.flashcards.application.card.DTO.CardPublicData;

public interface GetCardByIdUseCase {
    CardPublicData getCardById(Long cardId);
}
