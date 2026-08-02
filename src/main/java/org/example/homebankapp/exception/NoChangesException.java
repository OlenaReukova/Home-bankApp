package org.example.homebankapp.exception;

public class NoChangesException extends RuntimeException {
    public NoChangesException() {
        super("No changes provided");
    }
}
