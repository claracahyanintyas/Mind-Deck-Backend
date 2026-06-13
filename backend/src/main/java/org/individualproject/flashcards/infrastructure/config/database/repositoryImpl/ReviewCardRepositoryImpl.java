package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.persistence.ReviewCardRepository;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.ReviewCardJpaRepository;
import org.springframework.stereotype.Repository;

@Repository @RequiredArgsConstructor
public class ReviewCardRepositoryImpl implements ReviewCardRepository {
    private final ReviewCardJpaRepository reviewCardJpaRepository;
}
