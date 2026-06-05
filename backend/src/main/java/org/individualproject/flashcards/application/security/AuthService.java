package org.individualproject.flashcards.application.security;

import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.DTO.LoginInput;

public interface AuthService {
    jwtAuthOutput login(LoginInput loginInput);
}
