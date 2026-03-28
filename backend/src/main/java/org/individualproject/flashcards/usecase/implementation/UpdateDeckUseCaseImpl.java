package org.individualproject.flashcards.usecase.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;
import org.individualproject.flashcards.usecase.UpdateDeckUseCase;
import org.individualproject.flashcards.usecase.exception.DeckNotFoundException;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class UpdateDeckUseCaseImpl implements UpdateDeckUseCase {
    private DeckRepository deckRepository;
    public DeckPublicData updateDeck(Long id, UpdateDeckRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("deckId cannot be null or below 1");
        }

        var deck = deckRepository.findById(id)
                .orElseThrow(DeckNotFoundException::new)
                .fromEntity();

        deck.updateDetails(request.name(),  request.description(), request.isPrivate());

        var savedDeck = deckRepository.save(DeckEntity.toEntity(deck));

        return new DeckPublicData(savedDeck.getId(), savedDeck.getName(),
                savedDeck.getDescription(), savedDeck.getCreatedAt(),
                savedDeck.getUpdatedAt(), savedDeck.isPrivate());
    }
}
