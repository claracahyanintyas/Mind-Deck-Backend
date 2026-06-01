package org.individualproject.flashcards.application.exception;

public class EmailTakenException extends RuntimeException {
    public EmailTakenException() {
        super("Email Taken");
    }
}
