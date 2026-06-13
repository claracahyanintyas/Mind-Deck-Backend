package org.individualproject.flashcards.infrastructure.config.database.JpaRepository;

import org.individualproject.flashcards.infrastructure.config.database.entity.ReviewCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewCardJpaRepository extends JpaRepository<ReviewCardEntity, UUID> {
}
