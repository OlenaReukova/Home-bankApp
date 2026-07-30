package org.example.homebankapp.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String id;

    public UserNotFoundException(String id) {
        super(String.format("User not found with id: %s", id));
        this.id = id;
    }
}
