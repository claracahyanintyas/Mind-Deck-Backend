package org.individualproject.flashcards.application.review.implementation;

import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.application.persistence.ReviewRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.individualproject.flashcards.domain.card.ContentType;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.domain.review.Review;
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
class StartReviewUseCaseImplTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StartReviewUseCaseImpl startReviewUseCaseImpl;

    @Test
    void startReview_ValidUserAndDeckWithCards_ShouldReturnReviewPublicData() {
        // Arrange
        String username = "guest";
        Long deckId = 1L;
        User user = new User(username);

        // Setting up a deck that has 1 flashcard inside it to satisfy the non-empty check
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(new CardSide("front", ContentType.PLAIN_TEXT), new CardSide( "Back", ContentType.PLAIN_TEXT)));
        Deck deck = new Deck(deckId, "Languages", "Spanish", OffsetDateTime.now(), OffsetDateTime.now(), false, user, cards);

        // The review object that your repository.save() call will wrap and return
        Review expectedSavedReview = new Review(user, deck, new ArrayList<>());
        // (If your domain model assigns a UUID on instantiation or map, you can reflect it here)

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(deck));
        when(reviewRepository.save(any(Review.class))).thenReturn(expectedSavedReview);

        // Act
        var result = startReviewUseCaseImpl.startReview(username, deckId);

        // Assert
        assertNotNull(result);
        assertEquals(deckId, result.deck().id()); // Assuming result.deckId() matches your DTO getter
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void startReview_EmptyDeck_ShouldThrowIllegalStateException() {
        // Arrange
        String username = "guest";
        Long deckId = 1L;
        User user = new User(username);
        Deck emptyDeck = new Deck(deckId, "Empty", "", OffsetDateTime.now(), OffsetDateTime.now(), false, user, new ArrayList<>());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(deckRepository.findById(deckId)).thenReturn(Optional.of(emptyDeck));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> startReviewUseCaseImpl.startReview(username, deckId));
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void startReview_InvalidDeckId_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> startReviewUseCaseImpl.startReview("guest", -5L));
        verifyNoInteractions(userRepository, deckRepository, reviewRepository);
    }

    @Test
    void startReview_UserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        String username = "unknown";
        Long deckId = 1L;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> startReviewUseCaseImpl.startReview(username, deckId));
        verifyNoInteractions(deckRepository, reviewRepository);
    }
}