package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.persistence.ReviewRepository;
import org.individualproject.flashcards.domain.review.Review;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.ReviewJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.mapper.ReviewEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository @RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {
    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review save(Review review) {
        return ReviewEntityMapper.toDomain(reviewJpaRepository.save(ReviewEntityMapper.toEntity(review)));
    }
    @Override
    public Optional<Review> findById(UUID reviewId) {
        return reviewJpaRepository.findById(reviewId).map(ReviewEntityMapper::toDomain);
    }
}
