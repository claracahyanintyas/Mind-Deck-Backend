package org.individualproject.flashcards.infrastructure;

import org.individualproject.flashcards.application.exception.CardNotFoundException;
import org.individualproject.flashcards.application.exception.UsernameTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Build an isolated MockMvc instance targeting our stub controller and advisor layer
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FakeTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequestWithErrorsList() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Invalid argument supplied"));
    }

    @Test
    void handleNotFoundExceptions_ShouldReturnNotFoundWithApiErrorStructure() throws Exception {
        mockMvc.perform(get("/test/card-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Card not found"))
                .andExpect(jsonPath("$.path").value("/test/card-not-found"));
    }

    @Test
    void handleConflictExceptions_ShouldReturnBadRequestWithApiErrorStructure() throws Exception {
        mockMvc.perform(get("/test/username-taken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Username taken"));
    }

    @Test
    void handleValidationExceptions_ShouldExtractValidationFieldErrorsList() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Title size must be between 1 and 30"));
    }

    // --- A minimal local stub controller to intentionally trigger the target exception filters ---
    @RestController
    static class FakeTestController {

        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Invalid argument supplied");
        }

        @GetMapping("/test/card-not-found")
        public void throwCardNotFound() {
            throw new CardNotFoundException();
        }

        @GetMapping("/test/username-taken")
        public void throwUsernameTaken() {
            throw new UsernameTakenException();
        }

        @GetMapping("/test/validation-error")
        public void throwValidationError() throws MethodArgumentNotValidException {
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError error = new FieldError("objectName", "title", "Title size must be between 1 and 30");
            when(bindingResult.getFieldErrors()).thenReturn(List.of(error));

            // Throw the standard Spring web validation exception manually
            throw new MethodArgumentNotValidException(
                    mock(MethodParameter.class),
                    bindingResult
            );
        }
    }
}