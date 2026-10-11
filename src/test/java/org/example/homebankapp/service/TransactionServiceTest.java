package org.example.homebankapp.service;

import org.example.homebankapp.controller.response.TransactionResponse;
import org.example.homebankapp.model.Transaction;
import org.example.homebankapp.model.TransactionType;
import org.example.homebankapp.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository);
    }

    @Test
    void saveTransaction_shouldPersistTransaction() {
        transactionService.saveTransaction("acc-1", null, "ACC-AAAAAAAA", null, TransactionType.DEPOSIT, BigDecimal.TEN);

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void saveTransaction_shouldPersistTransferWithToAccountId() {
        transactionService.saveTransaction("acc-from", "acc-to", "ACC-AAAAAAAA", "ACC-BBBBBBBB", TransactionType.TRANSFER, new BigDecimal("100.00"));

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getTransactionsByAccountId_shouldReturnMappedList() {
        Transaction t = new Transaction();
        t.setId("txn-1");
        t.setAccountId("acc-1");
        t.setFromAccountNumber("ACC-AAAAAAAA");
        t.setType(TransactionType.DEPOSIT);
        t.setAmount(new BigDecimal("100.00"));
        t.setCreatedAt(LocalDateTime.now());

        when(transactionRepository.findAllByParticipantAccountId(eq("acc-1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(t)));

        Page<TransactionResponse> result = transactionService.getTransactionsByAccountId("acc-1", Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("txn-1", result.getContent().get(0).id());
        assertEquals(TransactionType.DEPOSIT, result.getContent().get(0).type());
        assertEquals(new BigDecimal("100.00"), result.getContent().get(0).amount());
        assertEquals("ACC-AAAAAAAA", result.getContent().get(0).fromAccountNumber());
    }

    @Test
    void getTransactionsByAccountId_shouldReturnEmpty_whenNoTransactions() {
        when(transactionRepository.findAllByParticipantAccountId(eq("acc-none"), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<TransactionResponse> result = transactionService.getTransactionsByAccountId("acc-none", Pageable.unpaged());

        assertTrue(result.isEmpty());
    }
}
