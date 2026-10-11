package org.example.homebankapp.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositRequest(
        @NotNull
        @Positive(message = "Deposit amount should be positive")
        BigDecimal amount
) {
}
