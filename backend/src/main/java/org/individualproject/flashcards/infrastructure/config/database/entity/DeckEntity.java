package org.individualproject.flashcards.infrastructure.config.database.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UpdateTimestamp;
import org.individualproject.flashcards.domain.Deck;

import java.time.OffsetDateTime;

@Entity
@Table(name = "decks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", updatable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @NotNull
    @Column(name = "is_private")
    private boolean isPrivate;

    public static DeckEntity toEntity(Deck deck){
        return DeckEntity.builder()
                .id(deck.getId())
                .name(deck.getName())
                .description(deck.getDescription())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .isPrivate(deck.getIsPrivate())
                .build();
    }
    public Deck fromEntity(){
        return new Deck(id,name, description,createdAt,updatedAt,isPrivate);
    }
}
