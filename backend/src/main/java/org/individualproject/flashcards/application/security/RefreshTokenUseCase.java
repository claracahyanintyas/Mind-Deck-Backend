package org.individualproject.flashcards.application.security;

import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;

public interface RefreshTokenUseCase {
    jwtAuthOutput rotateSessionTokens(String refreshToken);
}
