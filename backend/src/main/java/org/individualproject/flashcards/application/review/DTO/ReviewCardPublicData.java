package org.individualproject.flashcards.application.review.DTO;

import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.domain.review.CardState;

import java.util.UUID;

public record ReviewCardPublicData(UUID id,
                                   CardPublicData card,
                                   int box,
                                   CardState state) {
}
