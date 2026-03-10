package org.individualproject.flashcards.usecase.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.domain.Deck;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.usecase.CreateDeckUseCase;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateDeckUseCaseImpl implements CreateDeckUseCase {
    private DeckRepository deckRepository;
    public DeckPublicData createDeck(CreateDeckRequest request) {
        if (request == null){
            throw new  IllegalArgumentException("Request cannot be null");
        }
        Deck deck = new Deck(request.name(), request.description(), request.isPrivate());

        var savedDeck = deckRepository.save(DeckEntity.toEntity(deck))
                .fromEntity();

        return new DeckPublicData(savedDeck.getId(), savedDeck.getName(), savedDeck.getDescription(), savedDeck.getCreatedAt(),
                savedDeck.getUpdatedAt(), savedDeck.getIsPrivate());
    }
}
