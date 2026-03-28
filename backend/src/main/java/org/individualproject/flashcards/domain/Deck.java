package org.individualproject.flashcards.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter @AllArgsConstructor
public class Deck {
    private Long id;
    private String name;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Boolean isPrivate;

    public Deck(String name, String description, Boolean isPrivate) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        this.description = description;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.isPrivate = isPrivate;
    }
//    public void setName(String newName) {
//        if (newName == null || newName.isEmpty()) {
//            throw new IllegalArgumentException("name cannot be null");
//        }
//        this.name = newName;
//        this.updatedAt = OffsetDateTime.now();
//    }
//    public void setDescription(String newDescription) {
//        this.description = newDescription;
//        this.updatedAt = OffsetDateTime.now();
//    }
//    public void setIsPrivate(Boolean newIsPrivate) {
//        this.isPrivate = newIsPrivate;
//        this.updatedAt = OffsetDateTime.now();
//    }
    public void updateDetails(String newName, String newDescription, Boolean newIsPrivate) {
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }
            this.name = newName;
            this.description = newDescription;
            this.isPrivate = newIsPrivate;
            this.updatedAt = OffsetDateTime.now();
    }



}
