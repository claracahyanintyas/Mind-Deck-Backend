package org.individualproject.flashcards.infrastructure.config.database.JpaRepository;

import org.individualproject.flashcards.infrastructure.config.database.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, UUID> {
}
