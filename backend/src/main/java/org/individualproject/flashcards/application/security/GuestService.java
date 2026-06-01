package org.individualproject.flashcards.application.security;

import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;

public interface GuestService {
    jwtGuestOutput createGuestSession();
}
