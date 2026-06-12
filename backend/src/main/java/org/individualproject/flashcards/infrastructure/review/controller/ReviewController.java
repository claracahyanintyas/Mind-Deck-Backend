package org.individualproject.flashcards.infrastructure.review.controller;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.review.DTO.ProgressOutput;
import org.individualproject.flashcards.application.review.DTO.ReviewPublicData;
import org.individualproject.flashcards.application.review.ProcessReviewChoiceUseCase;
import org.individualproject.flashcards.application.review.StartReviewUseCase;
import org.individualproject.flashcards.domain.review.ReviewChoice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final StartReviewUseCase startReviewUseCase;
    private final ProcessReviewChoiceUseCase processReviewChoiceUseCase;
    @PostMapping("/deck/{id}")
    public ResponseEntity<ReviewPublicData> startReview(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var review = startReviewUseCase.startReview(authentication.getName(), id);
        return ResponseEntity.ok().body(review);
    }
    @PostMapping("/{reviewId}/cards/{cardId}")
    public ResponseEntity<ProgressOutput> submitCardReview(
            @PathVariable UUID reviewId,
            @PathVariable UUID cardId,
            @RequestParam ReviewChoice choice) {

        ProgressOutput progress = processReviewChoiceUseCase.processReviewChoice(reviewId, cardId, choice);
        return ResponseEntity.ok(progress);
    }
}
