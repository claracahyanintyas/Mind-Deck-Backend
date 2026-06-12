package org.individualproject.flashcards.application.card.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.card.DTO.EditCardCommand;
import org.individualproject.flashcards.application.card.EditCardUseCase;
import org.individualproject.flashcards.application.card.mapper.CardDTOMapper;
import org.individualproject.flashcards.application.exception.CardNotFoundException;
import org.individualproject.flashcards.application.persistence.CardRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class EditCardUseCaseImpl implements EditCardUseCase {
    private final CardRepository cardRepository;

    @Override
    public CardPublicData editCard(EditCardCommand card, Long cardId) {
        Card oldCard = cardRepository.findById(cardId).orElseThrow(CardNotFoundException::new);
        oldCard.updateCard(new CardSide(card.frontContent(), card.frontContentType()), new CardSide(card.backContent(), card.backContentType()));
        Card savedCard = cardRepository.findById(cardId).orElseThrow(CardNotFoundException::new);
        return CardDTOMapper.toDTO(savedCard);
    }
}
