package org.individualproject.flashcards.infrastructure.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.individualproject.flashcards.domain.card.ContentType;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.CardJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.UserJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.entity.CardEntity;
import org.individualproject.flashcards.infrastructure.config.database.entity.CardSideEmbeddable;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.DeckJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardJpaRepository cardRepository;

    @Autowired
    private DeckJpaRepository deckRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private final String baseUrl = "/api/cards";

    @Test
    void getCardById_ValidId_ReturnsCard() throws Exception {
        var user = new UserEntity(
                1L,
                "guest",
                "",
                "",
                OffsetDateTime.now(),
                false,
                new Array2DHashSet<>(),
                "",
                Instant.now()
        );
        var savedUser = userRepository.save(user);
        var deck = new DeckEntity(
                1L,
                "deck",
                "description",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                false,
                user,
                new ArrayList<>()
        );

        var savedDeck = deckRepository.save(deck);

        var card = new CardEntity(
                1L,
                savedDeck,
                new CardSideEmbeddable("front", ContentType.PLAIN_TEXT),
                new CardSideEmbeddable("back", ContentType.PLAIN_TEXT),
                OffsetDateTime.now(),
                OffsetDateTime.now()

        );


        var savedCard = cardRepository.save(card);

        mockMvc.perform(get(baseUrl + "/" + savedCard.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCard.getId()))
                .andExpect(jsonPath("$.frontContent").value("front"))
                .andExpect(jsonPath("$.backContent").value("back"));
    }

    @Test
    void getCardById_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(get(baseUrl + "/" + -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCardById_NotFound_ThrowsException() throws Exception {
        mockMvc.perform(get(baseUrl + "/" + 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCard_ReturnsNoContent() throws Exception {
        var user = new UserEntity(
                1L,
                "guest",
                "",
                "",
                OffsetDateTime.now(),
                false,
                new Array2DHashSet<>(),
                "",
                Instant.now()
        );
        var savedUser = userRepository.save(user);
        var deck = new DeckEntity(
                1L,
                "deck",
                "description",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                false,
                savedUser,
                new ArrayList<>()
        );

        var savedDeck = deckRepository.save(deck);

        var card = new CardEntity(
                1L,
                savedDeck,
                new CardSideEmbeddable("front", ContentType.PLAIN_TEXT),
                new CardSideEmbeddable("back", ContentType.PLAIN_TEXT),
                OffsetDateTime.now(),
                OffsetDateTime.now()

        );

        var savedCard = cardRepository.save(card);

        mockMvc.perform(delete(baseUrl + "/" + savedCard.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCard_InvalidId_ThrowsException() throws Exception {
        mockMvc.perform(delete(baseUrl + "/" + -1L))
                .andExpect(status().isBadRequest());
    }
}