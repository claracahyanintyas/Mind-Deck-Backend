package org.individualproject.flashcards.infrastructure.config.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.individualproject.flashcards.domain.review.CardState;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "review_cards")
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @Enumerated(EnumType.STRING)
    private CardState state;

    private int box;

    @Column(name = "next_review_sequence")
    private int nextReviewSequence;
}