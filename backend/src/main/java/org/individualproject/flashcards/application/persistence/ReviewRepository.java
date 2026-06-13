package org.individualproject.flashcards.application.persistence;

import org.individualproject.flashcards.domain.review.Review;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(UUID reviewId);
}
