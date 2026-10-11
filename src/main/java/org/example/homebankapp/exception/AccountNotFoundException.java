package org.example.homebankapp.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {

    private final String id;

    public AccountNotFoundException(String id) {
        this(id, String.format("Account not found with id: %s", id));
    }

    private AccountNotFoundException(String id, String message) {
        super(message);
        this.id = id;
    }

    public static AccountNotFoundException byAccountNumber(String accountNumber) {
        return new AccountNotFoundException(accountNumber,
                String.format("Account not found with number: %s", accountNumber));
    }
}
