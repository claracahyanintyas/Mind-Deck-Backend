package org.individualproject.flashcards.infrastructure.config.database.JpaRepository;

import org.individualproject.flashcards.infrastructure.config.database.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {
}
