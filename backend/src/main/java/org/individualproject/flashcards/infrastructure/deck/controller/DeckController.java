package org.individualproject.flashcards.infrastructure.deck.controller;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.domain.Deck;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.usecase.CreateDeckUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/decks")
public class DeckController {
    private CreateDeckUseCase createDeckUseCase;
    @PostMapping()
    public DeckPublicData createDeck(@RequestBody CreateDeckRequest request) {
        return createDeckUseCase.createDeck(request);
    }
}
