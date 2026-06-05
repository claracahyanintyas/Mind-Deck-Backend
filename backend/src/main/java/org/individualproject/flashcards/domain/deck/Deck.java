package org.individualproject.flashcards.domain.deck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.individualproject.flashcards.domain.card.Card;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Deck {
    private Long id;
    private String name;
    private String description;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Boolean isPrivate;

    private List<Card> cards =  new ArrayList<>();

    public Deck(String name, String description, Boolean isPrivate) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        this.description = description;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.isPrivate = isPrivate;
    }
    public Deck(Long id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt, Boolean isPrivate, List<Card> cards) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPrivate = isPrivate;
        this.cards = cards;
    }
    public void updateDetails(String newName, String newDescription, Boolean newIsPrivate) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
            this.name = newName;
            this.description = newDescription;
            this.isPrivate = newIsPrivate;
            this.updatedAt = OffsetDateTime.now();
    }
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        this.cards.add(card);
    }
    public void removeCard(Long id) {
        if (id == null || id <= 0)  {
            throw new IllegalArgumentException("card cannot be null");
        }
        cards.removeIf(card -> card.getId().equals(id));
    }


}
