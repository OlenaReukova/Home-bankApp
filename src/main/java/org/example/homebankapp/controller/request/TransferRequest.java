package org.example.homebankapp.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Account number is required")
        String toAccountNumber,

        @NotNull
        @Positive(message = "Amount should be positive")
        BigDecimal amount
) {
}
