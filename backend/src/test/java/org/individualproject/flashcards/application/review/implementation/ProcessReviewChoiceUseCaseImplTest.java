package org.individualproject.flashcards.application.review.implementation;

import org.individualproject.flashcards.application.persistence.ReviewRepository;
import org.individualproject.flashcards.application.review.DTO.ProgressOutput;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.individualproject.flashcards.domain.card.ContentType;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.domain.review.*;
import org.individualproject.flashcards.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessReviewChoiceUseCaseImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ProcessReviewChoiceUseCaseImpl processReviewChoiceUseCaseImpl;

    @Test
    void processReviewChoice_WithNextCardAvailable_ShouldReturnProgressWithNextCard() {
        // Arrange
        UUID reviewId = UUID.randomUUID();
        UUID currentCardId = UUID.randomUUID();
        UUID nextCardId = UUID.randomUUID();

        User user = new User("guest");
        Deck deck = new Deck(1L, "Deck", "", OffsetDateTime.now(), OffsetDateTime.now(), false, user, new ArrayList<>());
        Card flashcard = new Card(new CardSide("Q", ContentType.PLAIN_TEXT), new CardSide( "A", ContentType.PLAIN_TEXT));

        // Create 2 ReviewCards: one that we are reviewing right now, and one remaining ACTIVE
        ReviewCard currentReviewCard = new ReviewCard(currentCardId, flashcard, 1, CardState.ACTIVE, 0);
        ReviewCard nextReviewCard = new ReviewCard(nextCardId, flashcard, 1, CardState.ACTIVE, 0);

        List<ReviewCard> reviewCards = new ArrayList<>(List.of(currentReviewCard, nextReviewCard));
        Review activeReview = new Review(reviewId, user, deck, OffsetDateTime.now(), reviewCards, 0);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(activeReview));

        // Act
        ProgressOutput result = processReviewChoiceUseCaseImpl.processReviewChoice(reviewId, currentCardId, ReviewChoice.UNSURE);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.reviewId());
        assertFalse(result.isFinished());
        assertNotNull(result.nextCard());
        verify(reviewRepository, times(1)).save(activeReview);
    }

    @Test
    void processReviewChoice_WhenAllCardsArchived_ShouldReturnSessionFinished() {
        // Arrange
        UUID reviewId = UUID.randomUUID();
        UUID currentCardId = UUID.randomUUID();

        User user = new User("guest");
        Deck deck = new Deck(1L, "Deck", "", OffsetDateTime.now(), OffsetDateTime.now(), false, user, new ArrayList<>());
        Card flashcard = new Card(new CardSide("Q", ContentType.PLAIN_TEXT), new CardSide( "A", ContentType.PLAIN_TEXT));

        // Single card that will become ARCHIVED after we pick the choice
        ReviewCard currentReviewCard = new ReviewCard(currentCardId, flashcard, 1, CardState.ACTIVE, 0) {
            @Override
            public boolean isArchived() {
                return true; // Force archive state for session completion checking
            }
            @Override
            public CardState getState() {
                return CardState.ARCHIVED;
            }
        };

        List<ReviewCard> reviewCards = new ArrayList<>(List.of(currentReviewCard));
        Review activeReview = new Review(reviewId, user, deck, OffsetDateTime.now(), reviewCards, 0);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(activeReview));

        // Act
        ProgressOutput result = processReviewChoiceUseCaseImpl.processReviewChoice(reviewId, currentCardId, ReviewChoice.REMEMBER);

        // Assert
        assertNotNull(result);
        assertTrue(result.isFinished());
        assertNull(result.nextCard());
        assertEquals(100.0, result.progressPercentage());
        verify(reviewRepository, times(1)).save(activeReview);
    }

    @Test
    void processReviewChoice_ReviewNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        UUID fakeReviewId = UUID.randomUUID();
        when(reviewRepository.findById(fakeReviewId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                processReviewChoiceUseCaseImpl.processReviewChoice(fakeReviewId, UUID.randomUUID(), ReviewChoice.REMEMBER)
        );
        verify(reviewRepository, never()).save(any());
    }
}