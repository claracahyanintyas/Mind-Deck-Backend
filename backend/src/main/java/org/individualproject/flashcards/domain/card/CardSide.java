package org.individualproject.flashcards.domain.card;

public record CardSide(String content, ContentType contentType) {
    public CardSide {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or blank");
        }
        if (contentType == null) {
            contentType = ContentType.PLAIN_TEXT;
        }
    }
}
