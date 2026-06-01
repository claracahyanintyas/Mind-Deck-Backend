package org.individualproject.flashcards.application.security.DTO;

import org.individualproject.flashcards.application.security.AuthService;

public record LoginInput(String usernameOrEmail, String password){
}
