package org.individualproject.flashcards.domain.deck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.exception.UnauthorizedActionException;
import org.individualproject.flashcards.domain.user.User;

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
    private User createdBy;

    private List<Card> cards =  new ArrayList<>();

    public Deck(String name, String description, Boolean isPrivate, User createdBy) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        this.description = description;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.isPrivate = isPrivate;
        this.createdBy = createdBy;
    }
    public Deck(Long id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt, Boolean isPrivate, User createdBy, List<Card> cards) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPrivate = isPrivate;
        this.createdBy = createdBy;
        this.cards = cards;
    }
    public void updateDetails(String newName, String newDescription, Boolean newIsPrivate, String requestingUsername) {
        if (!createdBy.getUsername().equals(requestingUsername)) {
            throw new UnauthorizedActionException("Only creator can update this deck");
        }
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
            this.name = newName;
            this.description = newDescription;
            this.isPrivate = newIsPrivate;
            this.updatedAt = OffsetDateTime.now();
    }
    public void addCard(Card card, String requestingUsername) {
        if (!createdBy.getUsername().equals(requestingUsername)) {
            throw new UnauthorizedActionException("Only creator can update this deck");
        }
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        this.cards.add(card);
    }
    public void removeCard(Long id, String requestingUsername) {
        if (!createdBy.getUsername().equals(requestingUsername)) {
            throw new UnauthorizedActionException("Only creator can update this deck");
        }
        if (id == null || id <= 0)  {
            throw new IllegalArgumentException("card cannot be null");
        }
        cards.removeIf(card -> card.getId().equals(id));
    }


}
