package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.CardRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.CardJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.mapper.CardEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class CardRepositoryImpl implements CardRepository {
    private CardJpaRepository cardJpaRepository;
    @Override
    public Optional<Card> findById(Long id) {
        return cardJpaRepository.findById(id).map(CardEntityMapper::fromEntity);
    }

    @Override
    public void deleteById(Long id) {
        cardJpaRepository.deleteById(id);
    }
}
