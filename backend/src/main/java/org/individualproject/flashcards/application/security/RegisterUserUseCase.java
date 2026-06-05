package org.individualproject.flashcards.application.security;

import org.individualproject.flashcards.application.security.DTO.RegisterCommand;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;

public interface RegisterUserUseCase {
    jwtAuthOutput registerUser(RegisterCommand registerCommand, String guestName);
}
