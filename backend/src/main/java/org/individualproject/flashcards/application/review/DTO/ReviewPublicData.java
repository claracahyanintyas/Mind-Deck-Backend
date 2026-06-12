package org.individualproject.flashcards.application.review.DTO;

import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.domain.review.ReviewCard;
import org.individualproject.flashcards.domain.user.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ReviewPublicData(UUID id,
                               UserPublicData user,
                               DeckPublicData deck,
                               OffsetDateTime startedAt,
                               List<ReviewCardPublicData>reviewCards,
                               int totalCards) {
}
