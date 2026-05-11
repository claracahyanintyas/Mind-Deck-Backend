package org.individualproject.flashcards.domain.card;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class Card {
    private Long id;
    private CardSide frontSide;
    private CardSide backSide;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Card(CardSide frontSide, CardSide backSide) {
        this.frontSide = frontSide;
        this.backSide = backSide;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }
    public Card(Long id, CardSide frontSide, CardSide backSide,  OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.frontSide = frontSide;
        this.backSide = backSide;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public void updateFrontSide(CardSide frontSide) {
        if (frontSide == null) {
            throw new IllegalArgumentException("frontSide is null");
        }
        this.frontSide = frontSide;
        this.updatedAt = OffsetDateTime.now();
    }
    public void updateBackSide(CardSide backSide) {
        if (backSide == null) {
            throw new IllegalArgumentException("backSide is null");
        }
        this.backSide = backSide;
        this.updatedAt = OffsetDateTime.now();
    }
    public void updateCard(CardSide frontSide, CardSide backSide) {
        if (frontSide == null) {
            throw new IllegalArgumentException("frontSide is null");
        }
        if (backSide == null) {
            throw new IllegalArgumentException("backSide is null");
        }
        this.frontSide = frontSide;
        this.backSide = backSide;
        this.updatedAt = OffsetDateTime.now();
    }
}
