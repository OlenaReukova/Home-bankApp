package org.example.homebankapp.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequest(
        @NotNull
        @Positive(message = "Withdraw amount should be positive")
        BigDecimal amount
) {
}
