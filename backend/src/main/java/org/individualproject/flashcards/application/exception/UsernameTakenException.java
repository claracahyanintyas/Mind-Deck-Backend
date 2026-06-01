package org.individualproject.flashcards.application.exception;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException() {
        super("Username taken");
    }
}
