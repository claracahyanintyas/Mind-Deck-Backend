package org.individualproject.flashcards.application.exception;

public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException() {
        super("Deck not found");
    }
}
