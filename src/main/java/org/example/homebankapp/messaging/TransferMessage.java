package org.example.homebankapp.messaging;

import java.math.BigDecimal;

public record TransferMessage(
        String fromAccountId,
        String toAccountId,
        BigDecimal amount
) {
}
