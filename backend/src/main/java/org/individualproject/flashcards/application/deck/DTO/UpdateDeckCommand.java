package org.individualproject.flashcards.application.deck.DTO;

public record UpdateDeckCommand(String name, String description, Boolean isPrivate) {
}
