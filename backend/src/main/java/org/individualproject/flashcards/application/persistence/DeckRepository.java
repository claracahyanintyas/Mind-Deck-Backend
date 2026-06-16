package org.individualproject.flashcards.application.persistence;

import org.individualproject.flashcards.domain.deck.Deck;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface DeckRepository {
    Optional<Deck> findById(Long id);
    Deck save(Deck deck);
    Collection<Deck> findAll();
    Deck saveAndFlush(Deck deck);
    Collection<Deck> findAllPublicDecks();
}
