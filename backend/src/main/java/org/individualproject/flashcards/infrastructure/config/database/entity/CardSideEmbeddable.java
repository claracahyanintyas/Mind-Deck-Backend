package org.individualproject.flashcards.infrastructure.config.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.individualproject.flashcards.domain.card.ContentType;

@Embeddable @Data @NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSideEmbeddable {

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

}