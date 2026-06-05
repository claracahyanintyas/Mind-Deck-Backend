package org.individualproject.flashcards.application.security.DTO;

import lombok.Builder;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;

public record jwtAuthOutput(
        String token,
        String refreshToken,
        UserPublicData user
) {
}