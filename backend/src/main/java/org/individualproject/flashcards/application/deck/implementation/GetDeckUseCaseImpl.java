package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.deck.GetDeckUseCase;
import org.individualproject.flashcards.application.exception.DeckNotFoundException;
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
                deck.getCreatedAt(), deck.getUpdatedAt(), deck.getIsPrivate());
    }
}
