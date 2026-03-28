package org.individualproject.flashcards.usecase.exception;

public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException() {
        super("Deck not found");
    }
}
