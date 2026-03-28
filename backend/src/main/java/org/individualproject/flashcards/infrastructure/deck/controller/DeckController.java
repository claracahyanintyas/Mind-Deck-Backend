package org.individualproject.flashcards.infrastructure.deck.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;
import org.individualproject.flashcards.usecase.*;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/api/decks")
public class DeckController {
    private CreateDeckUseCase createDeckUseCase;
    private UpdateDeckUseCase updateDeckUseCase;
    private GetAllDecksUseCase getAllDecksUseCase;
    private GetDeckUseCase getDeckUseCase;
    private DeleteDeckUseCase deleteDeckUseCase;
    @PostMapping()
    public DeckPublicData createDeck(@Valid @RequestBody CreateDeckRequest request) {
        return createDeckUseCase.createDeck(request);
    }
    @PutMapping("/{id}")
    public DeckPublicData updateDeck(@PathVariable Long id ,@Valid @RequestBody UpdateDeckRequest request) {
        return  updateDeckUseCase.updateDeck(id, request);
    }
    @GetMapping()
    public Collection<DeckPublicData> getAllDecks() {
        return getAllDecksUseCase.getAllDecks();
    }
    @GetMapping("/{id}")
    public DeckPublicData getDeck(@PathVariable Long id) {
        return getDeckUseCase.getDeck(id);
    }
    @DeleteMapping("/{id}")
    public void deleteDeck(@PathVariable Long id) {
        deleteDeckUseCase.deleteDeck(id);
    }
}
