package org.individualproject.flashcards.application.review.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.exception.DeckNotFoundException;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.application.persistence.ReviewRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.review.DTO.ReviewPublicData;
import org.individualproject.flashcards.application.review.StartReviewUseCase;
import org.individualproject.flashcards.application.review.mapper.ReviewDTOMapper;
import org.individualproject.flashcards.domain.review.CardState;
import org.individualproject.flashcards.domain.review.Review;
import org.individualproject.flashcards.domain.review.ReviewCard;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @AllArgsConstructor
public class StartReviewUseCaseImpl implements StartReviewUseCase {
    private DeckRepository deckRepository;
    private ReviewRepository reviewRepository;
    private UserRepository userRepository;
    @Transactional
    public ReviewPublicData startReview(String username, Long deckId) {
        if (username == null || deckId == null || username.isEmpty()) {
            throw new IllegalArgumentException("username and deck cannot be null.");
        }
        if (deckId <= 0){
            throw new IllegalArgumentException("deckId cannot be 0 or negative.");
        }
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("username not found."));
        var deck = deckRepository.findById(deckId).orElseThrow(DeckNotFoundException::new);
        if (deck.getCards().isEmpty()) {
            throw new IllegalStateException("Cannot start a review on an empty deck");
        }

        List<ReviewCard> reviewCards = deck.getCards().stream()
                .map(card -> new ReviewCard(card, 1, CardState.ACTIVE, 0))
                .collect(Collectors.toList());

        Review newReview = new Review(user, deck, reviewCards);
        var savedReview = reviewRepository.save(newReview);
        return ReviewDTOMapper.toReviewPublicData(savedReview);
    }
}
