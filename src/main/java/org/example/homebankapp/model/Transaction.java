package org.example.homebankapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    private String accountId;

    private String toAccountId;

    private String fromAccountNumber;

    private String toAccountNumber;

    private TransactionType type;

    private BigDecimal amount;

    @CreatedDate
    private LocalDateTime createdAt;
}
