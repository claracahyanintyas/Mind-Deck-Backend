package org.individualproject.flashcards.infrastructure.card.DTO;

import jakarta.validation.constraints.NotBlank;
import org.individualproject.flashcards.domain.card.ContentType;

public record EditCardRequest (@NotBlank(message = "content cannot be blank")
                               String frontContent,
                               ContentType frontContentType,
                               @NotBlank(message = "content cannot be blank")
                               String backContent,
                               ContentType backContentType){
}
