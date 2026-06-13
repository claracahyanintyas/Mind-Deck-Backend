package org.individualproject.flashcards.application.card;

import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.card.DTO.EditCardCommand;

public interface EditCardUseCase {
    CardPublicData  editCard(EditCardCommand card, Long cardId);
}
