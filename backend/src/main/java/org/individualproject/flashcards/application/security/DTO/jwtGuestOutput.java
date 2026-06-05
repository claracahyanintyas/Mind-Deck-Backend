package org.individualproject.flashcards.application.security.DTO;

import org.individualproject.flashcards.application.user.DTO.UserPublicData;

public record jwtGuestOutput(String token, String refreshToken, UserPublicData user) {
}
