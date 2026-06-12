package org.individualproject.flashcards.infrastructure.card;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.card.DTO.EditCardCommand;
import org.individualproject.flashcards.application.card.DeleteCardUseCase;
import org.individualproject.flashcards.application.card.EditCardUseCase;
import org.individualproject.flashcards.application.card.GetCardByIdUseCase;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.infrastructure.card.DTO.EditCardRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cards")
public class CardController {
    private DeleteCardUseCase deleteCardUseCase;
    private GetCardByIdUseCase getCardByIdUseCase;
    private EditCardUseCase editCardUseCase;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        deleteCardUseCase.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<CardPublicData> getCardById(@PathVariable Long id) {
        CardPublicData result = getCardByIdUseCase.getCardById(id);
        return ResponseEntity.ok().body(result);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CardPublicData> editCard(@PathVariable Long id, @RequestBody EditCardRequest editCardRequest) {
        EditCardCommand command = new EditCardCommand(editCardRequest.frontContent(), editCardRequest.frontContentType(),
                editCardRequest.backContent(), editCardRequest.backContentType());
        CardPublicData result = editCardUseCase.editCard(command, id);
        return ResponseEntity.ok().body(result);
    }

}
