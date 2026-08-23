package org.example.homebankapp.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    private final String email;

    public EmailAlreadyExistsException(String email){
        super(String.format("User with email %s already exists", email));
        this.email=email;
    }
}
