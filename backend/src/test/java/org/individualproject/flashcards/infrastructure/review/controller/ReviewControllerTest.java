package org.individualproject.flashcards.infrastructure.review.controller;

import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.individualproject.flashcards.domain.card.ContentType;
import org.individualproject.flashcards.domain.review.CardState;
import org.individualproject.flashcards.domain.review.ReviewChoice;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.*;
import org.individualproject.flashcards.infrastructure.config.database.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private DeckJpaRepository deckRepository;

     @Autowired
     private ReviewJpaRepository reviewRepository;

     @Autowired
     private CardJpaRepository cardRepository;

     @Autowired
     private ReviewCardJpaRepository reviewCardRepository;

    private final String baseUrl = "/api/reviews";
    @Autowired
    private ReviewCardJpaRepository reviewCardJpaRepository;

    @Test
    @WithMockUser(username = "guest")
    void startReview_ValidDeckId_ReturnsReviewPublicData() throws Exception {
        // 1. Arrange - Setup real DB records just like your deck tests
        var user = new UserEntity(
                1L, "guest", "", "", OffsetDateTime.now(),
                false, new Array2DHashSet<>(), "", Instant.now()
        );
        var savedUser = userRepository.save(user);

        var deck = new DeckEntity(
                null, "Study Deck", "Description", OffsetDateTime.now(),
                OffsetDateTime.now(), false, savedUser, new ArrayList<>()
        );
        var savedDeck = deckRepository.save(deck);

        var card = new CardEntity(null, savedDeck, new CardSideEmbeddable("front", ContentType.PLAIN_TEXT),
                new CardSideEmbeddable("back", ContentType.PLAIN_TEXT), OffsetDateTime.now(), OffsetDateTime.now());
        var savedCard = cardRepository.save(card);
        var cardSet = new ArrayList<CardEntity>();
        cardSet.add(savedCard);

        savedDeck.setCards(cardSet);
        deckRepository.save(savedDeck);

        // 2. Act & Assert
        mockMvc.perform(post(baseUrl + "/deck/" + savedDeck.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.deck").exists());
    }

    @Test
    @WithMockUser(username = "guest")
    void submitCardReview_ValidChoice_ReturnsProgressOutput() throws Exception {
        // 1. Arrange - Setup real records spanning User -> Deck -> Card -> Active Review session
        var user = new UserEntity(
                1L, "guest", "", "", OffsetDateTime.now(),
                false, new Array2DHashSet<>(), "", Instant.now()
        );
        var savedUser = userRepository.save(user);

        var deck = new DeckEntity(
                null, "Study Deck", "Description", OffsetDateTime.now(),
                OffsetDateTime.now(), false, savedUser, new ArrayList<>()
        );
        var savedDeck = deckRepository.save(deck);

        var card = new CardEntity(null, savedDeck, new CardSideEmbeddable("front", ContentType.PLAIN_TEXT),
                 new CardSideEmbeddable("back", ContentType.PLAIN_TEXT), OffsetDateTime.now(), OffsetDateTime.now());
        var savedCard = cardRepository.save(card);
        var review = new ReviewEntity(UUID.randomUUID(), savedUser, savedDeck, OffsetDateTime.now(), new HashSet<>(), 0);
        var savedReview = reviewRepository.save(review);
        var reviewCard = new ReviewCardEntity(UUID.randomUUID(),savedReview, savedCard, CardState.ACTIVE, 1, 1 );
        var savedReviewCard = reviewCardJpaRepository.save(reviewCard);
        var reviewCardsSet = new HashSet<ReviewCardEntity>();
        reviewCardsSet.add(savedReviewCard);

        savedReview.setReviewCards(reviewCardsSet);
        reviewRepository.save(savedReview);


        // 2. Act & Assert
        mockMvc.perform(post(baseUrl + "/" + savedReview.getId() + "/cards/" + savedReviewCard.getId())
                        .param("choice", ReviewChoice.REMEMBER.name()) // Assuming GOOD is a valid enum choice
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercentage").exists())
                .andExpect(jsonPath("$.isFinished").exists());
    }
}