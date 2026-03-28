package org.individualproject.flashcards.usecase.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.usecase.DeleteDeckUseCase;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class DeleteDeckUseCaseImpl implements DeleteDeckUseCase {
    private DeckRepository deckRepository;
    public void deleteDeck(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        deckRepository.deleteById(id);
    }
}
