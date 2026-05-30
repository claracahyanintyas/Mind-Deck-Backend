package org.individualproject.flashcards.application.card.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.card.GetCardByIdUseCase;
import org.individualproject.flashcards.application.exception.CardNotFoundException;
import org.individualproject.flashcards.application.persistence.CardRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetCardByIdUseCaseImpl implements GetCardByIdUseCase {
    private CardRepository cardRepository;
    public CardPublicData getCardById(Long id) {
        if (id == null || id <= 0) {
            throw new  IllegalArgumentException("id cannot be null or lower than 1");
        }
        Card result = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
        return new CardPublicData(result.getId(), result.getFrontSide().content(), result.getFrontSide().contentType(),
                result.getBackSide().content(), result.getBackSide().contentType(), result.getCreatedAt(), result.getUpdatedAt());
    }
}
