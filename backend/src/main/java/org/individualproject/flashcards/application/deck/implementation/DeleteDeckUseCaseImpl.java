package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.DeckJpaRepository;
import org.individualproject.flashcards.application.deck.DeleteDeckUseCase;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class DeleteDeckUseCaseImpl implements DeleteDeckUseCase {
    private DeckJpaRepository deckRepository;
    public void deleteDeck(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        deckRepository.deleteById(id);
    }
}
