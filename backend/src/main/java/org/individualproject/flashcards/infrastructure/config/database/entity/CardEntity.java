package org.individualproject.flashcards.infrastructure.config.database.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private DeckEntity deck;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "content", column = @Column(name = "front_content")),
            @AttributeOverride(name = "contentType", column = @Column(name = "front_content_type"))
    })
    private CardSideEmbeddable frontSide;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "content", column = @Column(name = "back_content")),
            @AttributeOverride(name = "contentType", column = @Column(name = "back_content_type"))
    })
    private CardSideEmbeddable backSide;

    @NotNull
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
