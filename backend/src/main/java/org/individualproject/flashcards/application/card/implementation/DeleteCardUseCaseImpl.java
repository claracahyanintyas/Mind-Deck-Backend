package org.individualproject.flashcards.application.card.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.card.DeleteCardUseCase;
import org.individualproject.flashcards.application.persistence.CardRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeleteCardUseCaseImpl implements DeleteCardUseCase {
    private CardRepository cardRepository;

    @Override
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }
}
