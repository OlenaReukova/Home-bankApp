package org.example.homebankapp.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends RuntimeException {

    private final BigDecimal balance;
    private final BigDecimal requested;

    public InsufficientFundsException(BigDecimal balance, BigDecimal requested) {
        super(String.format("Insufficient funds: balance %.2f, requested %.2f", balance, requested));
        this.balance = balance;
        this.requested = requested;
    }
}
