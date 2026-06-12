package org.individualproject.flashcards.infrastructure.card.DTO;

import jakarta.validation.constraints.NotBlank;
import org.individualproject.flashcards.domain.card.ContentType;

public record EditCardRequest (@NotBlank
                               String frontContent,
                               ContentType frontContentType,
                               @NotBlank
                               String backContent,
                               ContentType backContentType){
}
