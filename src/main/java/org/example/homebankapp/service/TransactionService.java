package org.example.homebankapp.service;

import lombok.extern.slf4j.Slf4j;
import org.example.homebankapp.controller.response.TransactionResponse;
import org.example.homebankapp.model.Transaction;
import org.example.homebankapp.model.TransactionType;
import org.example.homebankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void saveTransaction(String accountId, String toAccountId,
                                String fromAccountNumber, String toAccountNumber,
                                TransactionType type, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setToAccountId(toAccountId);
        transaction.setFromAccountNumber(fromAccountNumber);
        transaction.setToAccountNumber(toAccountNumber);
        transaction.setType(type);
        transaction.setAmount(amount);

        transactionRepository.save(transaction);
        log.info("Transaction saved type={} from={} amount={}", type, fromAccountNumber, amount);
    }

    public Page<TransactionResponse> getTransactionsByAccountId(String accountId, Pageable pageable) {
        return transactionRepository.findAllByParticipantAccountId(accountId, pageable)
                .map(this::toResponse);
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getFromAccountNumber(),
                t.getToAccountNumber(),
                t.getCreatedAt()
        );
    }
}
