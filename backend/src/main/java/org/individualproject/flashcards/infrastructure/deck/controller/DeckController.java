package org.individualproject.flashcards.infrastructure.deck.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.deck.AddCardToDeckUseCase;
import org.individualproject.flashcards.application.card.DTO.AddCardCommand;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.infrastructure.deck.DTO.AddCardRequest;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.individualproject.flashcards.application.deck.DTO.CreateDeckCommand;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;
import org.individualproject.flashcards.application.deck.DTO.UpdateDeckCommand;
import org.individualproject.flashcards.application.deck.*;
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
    private AddCardToDeckUseCase addCardToDeckUseCase;

    @PostMapping()
    public ResponseEntity<DeckPublicData> createDeck(@Valid @RequestBody CreateDeckRequest request) {
        var command = new CreateDeckCommand(request.name(), request.description(), request.isPrivate());
        var result = createDeckUseCase.createDeck(command);
        return ResponseEntity.ok().body(result);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DeckPublicData> updateDeck(@PathVariable Long id ,@Valid @RequestBody UpdateDeckRequest request) {
        var command = new UpdateDeckCommand(request.name(), request.description(), request.isPrivate());
        var result = updateDeckUseCase.updateDeck(id, command);
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
    @PostMapping("/{id}/cards")
    public ResponseEntity<CardPublicData> addCardToDeck(@PathVariable Long id, @Valid @RequestBody AddCardRequest request) {
        var command = new AddCardCommand(id, request.frontContent(), request.frontContentType(), request.backContent(), request.backContentType());
        var result = addCardToDeckUseCase.addCardToDeck(command);
        return ResponseEntity.ok().body(result);
    }
}
