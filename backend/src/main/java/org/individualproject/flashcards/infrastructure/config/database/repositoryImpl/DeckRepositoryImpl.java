package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.DeckJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.mapper.DeckEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class DeckRepositoryImpl implements DeckRepository {
    private final DeckJpaRepository jpaRepository;

    @Override
    public Optional<Deck> findById(Long id) {
        return jpaRepository.findById(id).map(DeckEntityMapper::fromEntity);
    }

    @Override
    public Deck save(Deck deck) {
        DeckEntity entity = DeckEntityMapper.toEntity(deck);
        DeckEntity saved = jpaRepository.save(entity);
        return DeckEntityMapper.fromEntity(saved);
    }

    @Override
    public Collection<Deck> findAll() {
        return jpaRepository.findAll().stream().map(DeckEntityMapper::fromEntity).toList();
    }
    @Override
    public Deck saveAndFlush(Deck deck) {
        DeckEntity entity = DeckEntityMapper.toEntity(deck);
        DeckEntity saved = jpaRepository.saveAndFlush(entity);
        return DeckEntityMapper.fromEntity(saved);
    }
    @Override
    public Collection<Deck> findAllPublicDecks(){
        return jpaRepository.findAllByIsPrivateFalse().stream().map(DeckEntityMapper::fromEntity).toList();
    }
}
