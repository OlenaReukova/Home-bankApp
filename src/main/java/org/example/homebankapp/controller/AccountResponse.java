package org.example.homebankapp.controller;

import org.example.homebankapp.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String id,
        String userId,
        String accountNumber,
        BigDecimal baalance,
        Currency currency,
        LocalDateTime createdAt
) {
}
