package org.individualproject.flashcards.usecase.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.usecase.GetDeckUseCase;
import org.individualproject.flashcards.usecase.exception.DeckNotFoundException;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class GetDeckUseCaseImpl implements GetDeckUseCase {
    private DeckRepository deckRepository;

    @Override
    public DeckPublicData getDeck(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        var deck = deckRepository.findById(id).orElseThrow(DeckNotFoundException::new);
        return new DeckPublicData(deck.getId(), deck.getName(), deck.getDescription(),
                deck.getCreatedAt(), deck.getUpdatedAt(), deck.isPrivate());
    }
}
