package org.individualproject.flashcards.infrastructure.deck.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;
import org.individualproject.flashcards.usecase.*;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DeckPublicData> createDeck(@Valid @RequestBody CreateDeckRequest request) {
        var result = createDeckUseCase.createDeck(request);
        return ResponseEntity.ok().body(result);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DeckPublicData> updateDeck(@PathVariable Long id ,@Valid @RequestBody UpdateDeckRequest request) {
        var result = updateDeckUseCase.updateDeck(id, request);
        return ResponseEntity.ok().body(result);
    }
    @GetMapping()
    public ResponseEntity<Collection<DeckPublicData>> getAllDecks() {
        var result = getAllDecksUseCase.getAllDecks();
        return ResponseEntity.ok().body(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DeckPublicData> getDeck(@PathVariable Long id) {
        var result = getDeckUseCase.getDeck(id);
        return ResponseEntity.ok().body(result);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long id) {
        deleteDeckUseCase.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }
}
