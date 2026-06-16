package org.individualproject.flashcards.application.persistence;

import org.individualproject.flashcards.domain.card.Card;

import java.util.Optional;

public interface CardRepository {
    Optional<Card> findById(Long id);
    void deleteById(Long id);
    Optional<Card> save(Card card);
}
