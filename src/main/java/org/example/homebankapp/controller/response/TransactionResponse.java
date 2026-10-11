package org.example.homebankapp.controller.response;

import org.example.homebankapp.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        TransactionType type,
        BigDecimal amount,
        String fromAccountNumber,
        String toAccountNumber,
        LocalDateTime createdAt
) {
}
