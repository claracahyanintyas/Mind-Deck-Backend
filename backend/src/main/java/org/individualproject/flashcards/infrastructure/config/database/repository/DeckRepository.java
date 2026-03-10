package org.individualproject.flashcards.infrastructure.config.database.repository;

import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeckRepository extends JpaRepository<DeckEntity, Long>{
}
