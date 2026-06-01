package org.individualproject.flashcards.application.security.DTO;

public record RegisterCommand(
        String username,
        String email,
        String password
) {
}
