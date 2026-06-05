package org.individualproject.flashcards.application.user;

import org.individualproject.flashcards.application.user.DTO.UserPublicData;

public interface GetUserByUsernameUseCase {
    UserPublicData getUser(String username);
}
