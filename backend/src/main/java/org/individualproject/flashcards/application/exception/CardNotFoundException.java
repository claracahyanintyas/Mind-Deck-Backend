package org.individualproject.flashcards.application.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException() {
        super("Card not found");
    }
}
