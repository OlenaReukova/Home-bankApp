package org.example.homebankapp.service;

import org.example.homebankapp.controller.request.CreateAccountRequest;
import org.example.homebankapp.controller.request.DepositRequest;
import org.example.homebankapp.controller.request.TransferRequest;
import org.example.homebankapp.controller.request.WithdrawRequest;
import org.example.homebankapp.controller.response.AccountResponse;
import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.exception.AccountNotFoundException;
import org.example.homebankapp.exception.InsufficientFundsException;
import org.example.homebankapp.exception.UserNotFoundException;
import org.example.homebankapp.model.BankAccount;
import org.example.homebankapp.model.Currency;
import org.example.homebankapp.repository.BankAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    private BankAccountService bankAccountService;

    private static final String OWNER_EMAIL = "owner@example.com";
    private static final String OWNER_ID = "user-1";

    @BeforeEach
    void setUp() {
        bankAccountService = new BankAccountService(accountRepository, transactionService, userService);

        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getName()).thenReturn(OWNER_EMAIL);
        SecurityContext ctx = mock(SecurityContext.class);
        lenient().when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        // lenient: not all tests reach checkOwnership (e.g. account-not-found tests)
        lenient().when(userService.getUserByEmail(OWNER_EMAIL))
                .thenReturn(new UserResponse(OWNER_ID, "Owner", "User", OWNER_EMAIL, null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private BankAccount accountWithBalance(String id, BigDecimal balance) {
        BankAccount account = new BankAccount();
        account.setId(id);
        account.setUserId(OWNER_ID);
        account.setAccountNumber("ACC-TESTTEST");
        account.setBalance(balance);
        account.setCurrency(Currency.USD);
        return account;
    }

    // --- createAccount ---

    @Test
    void createAccount_shouldSaveAndReturnAccount() {
        CreateAccountRequest request = new CreateAccountRequest(OWNER_ID, Currency.USD, BigDecimal.ZERO);
        BankAccount saved = accountWithBalance("acc-1", BigDecimal.ZERO);

        when(userService.userExistsById(OWNER_ID)).thenReturn(true);
        when(accountRepository.save(any())).thenReturn(saved);

        AccountResponse result = bankAccountService.createAccount(request);

        assertNotNull(result);
        assertEquals("acc-1", result.id());
        assertEquals(Currency.USD, result.currency());
        verify(accountRepository).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("createAccount should throw AccessDeniedException when userId does not match current user")
    void createAccount_shouldThrow_whenNotOwner() {
        CreateAccountRequest request = new CreateAccountRequest("other-user-id", Currency.USD, BigDecimal.ZERO);

        when(userService.userExistsById("other-user-id")).thenReturn(true);

        assertThrows(AccessDeniedException.class,
                () -> bankAccountService.createAccount(request));

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("createAccount should throw UserNotFoundException when userId does not exist")
    void createAccount_shouldThrow_whenUserNotFound() {
        CreateAccountRequest request = new CreateAccountRequest("nonexistent-id", Currency.USD, BigDecimal.ZERO);

        when(userService.userExistsById("nonexistent-id")).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> bankAccountService.createAccount(request));

        verify(accountRepository, never()).save(any());
    }

    // --- getAccountsByUserId ---

    @Test
    void getAccountsByUserId_shouldReturnPage() {
        Page<BankAccount> page = new PageImpl<>(List.of(accountWithBalance("acc-1", BigDecimal.TEN)));
        when(accountRepository.findByUserId(eq(OWNER_ID), any(Pageable.class))).thenReturn(page);

        Page<AccountResponse> result = bankAccountService.getAccountsByUserId(OWNER_ID, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("acc-1", result.getContent().get(0).id());
    }

    @Test
    void getAccountsByUserId_shouldReturnEmptyPage_whenNoAccounts() {
        when(accountRepository.findByUserId(eq(OWNER_ID), any(Pageable.class))).thenReturn(Page.empty());

        Page<AccountResponse> result = bankAccountService.getAccountsByUserId(OWNER_ID, Pageable.unpaged());

        assertTrue(result.isEmpty());
    }

    // --- deposit ---

    @Test
    void deposit_shouldIncreaseBalance() {
        BankAccount account = accountWithBalance("acc-1", new BigDecimal("100.00"));
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse result = bankAccountService.deposit("acc-1", new DepositRequest(new BigDecimal("50.00")));

        assertEquals(new BigDecimal("150.00"), result.balance());
        verify(transactionService).saveTransaction(eq("acc-1"), isNull(), anyString(), isNull(), any(), eq(new BigDecimal("50.00")));
    }

    @Test
    @DisplayName("Deposit should throw IllegalArgumentException when amount is zero or negative")
    void deposit_shouldThrow_whenAmountNotPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.deposit("acc-1", new DepositRequest(BigDecimal.ZERO)));
        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.deposit("acc-1", new DepositRequest(new BigDecimal("-1"))));

        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("Deposit should throw AccountNotFoundException when account not found")
    void deposit_shouldThrow_whenAccountNotFound() {
        when(accountRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> bankAccountService.deposit("bad-id", new DepositRequest(BigDecimal.TEN)));

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deposit should throw AccessDeniedException when account belongs to another user")
    void deposit_shouldThrow_whenNotOwner() {
        BankAccount account = accountWithBalance("acc-1", new BigDecimal("100.00"));
        account.setUserId("other-user-id");
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));

        assertThrows(AccessDeniedException.class,
                () -> bankAccountService.deposit("acc-1", new DepositRequest(new BigDecimal("50.00"))));

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(transactionService);
    }

    // --- withdraw ---

    @Test
    void withdraw_shouldDecreaseBalance() {
        BankAccount account = accountWithBalance("acc-1", new BigDecimal("100.00"));
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse result = bankAccountService.withdraw("acc-1", new WithdrawRequest(new BigDecimal("30.00")));

        assertEquals(new BigDecimal("70.00"), result.balance());
        verify(transactionService).saveTransaction(eq("acc-1"), isNull(), anyString(), isNull(), any(), eq(new BigDecimal("30.00")));
    }

    @Test
    @DisplayName("Withdraw should throw IllegalArgumentException when amount is zero or negative")
    void withdraw_shouldThrow_whenAmountNotPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.withdraw("acc-1", new WithdrawRequest(BigDecimal.ZERO)));
        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.withdraw("acc-1", new WithdrawRequest(new BigDecimal("-1"))));

        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("Withdraw should throw InsufficientFundsException when balance is too low")
    void withdraw_shouldThrow_whenInsufficientFunds() {
        BankAccount account = accountWithBalance("acc-1", new BigDecimal("20.00"));
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class,
                () -> bankAccountService.withdraw("acc-1", new WithdrawRequest(new BigDecimal("50.00"))));

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(transactionService);
    }

    @Test
    @DisplayName("Withdraw should throw AccessDeniedException when account belongs to another user")
    void withdraw_shouldThrow_whenNotOwner() {
        BankAccount account = accountWithBalance("acc-1", new BigDecimal("100.00"));
        account.setUserId("other-user-id");
        when(accountRepository.findById("acc-1")).thenReturn(Optional.of(account));

        assertThrows(AccessDeniedException.class,
                () -> bankAccountService.withdraw("acc-1", new WithdrawRequest(new BigDecimal("30.00"))));

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(transactionService);
    }

    // --- transfer ---

    @Test
    void transfer_shouldMoveFundsBetweenAccounts() {
        BankAccount from = accountWithBalance("acc-from", new BigDecimal("200.00"));
        BankAccount to = accountWithBalance("acc-to", new BigDecimal("50.00"));
        to.setAccountNumber("ACC-TARGET");

        when(accountRepository.findById("acc-from")).thenReturn(Optional.of(from));
        when(accountRepository.findByAccountNumber("ACC-TARGET")).thenReturn(Optional.of(to));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse result = bankAccountService.transfer("acc-from",
                new TransferRequest("ACC-TARGET", new BigDecimal("100.00")));

        assertEquals(new BigDecimal("100.00"), result.balance());
        assertEquals(new BigDecimal("150.00"), to.getBalance());
        verify(transactionService).saveTransaction(eq("acc-from"), eq("acc-to"), anyString(), eq("ACC-TARGET"), any(), eq(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Transfer should throw IllegalArgumentException when amount is zero or negative")
    void transfer_shouldThrow_whenAmountNotPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.transfer("acc-from", new TransferRequest("ACC-TARGET", BigDecimal.ZERO)));
        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.transfer("acc-from", new TransferRequest("ACC-TARGET", new BigDecimal("-1"))));

        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("Transfer should throw InsufficientFundsException when balance is too low")
    void transfer_shouldThrow_whenInsufficientFunds() {
        BankAccount from = accountWithBalance("acc-from", new BigDecimal("10.00"));
        BankAccount to = accountWithBalance("acc-to", BigDecimal.ZERO);
        to.setAccountNumber("ACC-TARGET");

        when(accountRepository.findById("acc-from")).thenReturn(Optional.of(from));
        when(accountRepository.findByAccountNumber("ACC-TARGET")).thenReturn(Optional.of(to));

        assertThrows(InsufficientFundsException.class,
                () -> bankAccountService.transfer("acc-from",
                        new TransferRequest("ACC-TARGET", new BigDecimal("100.00"))));

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(transactionService);
    }

    @Test
    @DisplayName("Transfer should throw AccountNotFoundException when target account not found")
    void transfer_shouldThrow_whenTargetAccountNotFound() {
        BankAccount from = accountWithBalance("acc-from", new BigDecimal("200.00"));

        when(accountRepository.findById("acc-from")).thenReturn(Optional.of(from));
        when(accountRepository.findByAccountNumber("ACC-MISSING")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> bankAccountService.transfer("acc-from",
                        new TransferRequest("ACC-MISSING", new BigDecimal("50.00"))));
    }

    @Test
    @DisplayName("Transfer should throw IllegalArgumentException when transferring to the same account")
    void transfer_shouldThrow_whenSelfTransfer() {
        BankAccount from = accountWithBalance("acc-from", new BigDecimal("200.00"));

        when(accountRepository.findById("acc-from")).thenReturn(Optional.of(from));

        assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.transfer("acc-from",
                        new TransferRequest("ACC-TESTTEST", new BigDecimal("100.00"))));

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(transactionService);
    }

    @Test
    @DisplayName("Transfer should throw AccessDeniedException when from-account belongs to another user")
    void transfer_shouldThrow_whenNotOwner() {
        BankAccount from = accountWithBalance("acc-from", new BigDecimal("200.00"));
        from.setUserId("other-user-id");

        when(accountRepository.findById("acc-from")).thenReturn(Optional.of(from));

        assertThrows(AccessDeniedException.class,
                () -> bankAccountService.transfer("acc-from",
                        new TransferRequest("ACC-TARGET", new BigDecimal("100.00"))));

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(transactionService);
    }
}
