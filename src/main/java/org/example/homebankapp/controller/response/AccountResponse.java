package org.example.homebankapp.controller.response;

import org.example.homebankapp.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String id,
        String userId,
        String accountNumber,
        BigDecimal balance,
        Currency currency,
        LocalDateTime createdAt
) {
}
