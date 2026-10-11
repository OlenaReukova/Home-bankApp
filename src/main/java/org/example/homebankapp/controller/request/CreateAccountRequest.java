package org.example.homebankapp.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.homebankapp.model.Currency;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "User ID is required")
        String userId,

        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull
        @PositiveOrZero(message = "Initial balance must be 0 or positive")
        BigDecimal initialBalance) {

}
