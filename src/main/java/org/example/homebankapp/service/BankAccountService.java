package org.example.homebankapp.service;

import lombok.extern.slf4j.Slf4j;
import org.example.homebankapp.controller.request.CreateAccountRequest;
import org.example.homebankapp.controller.request.DepositRequest;
import org.example.homebankapp.controller.request.TransferRequest;
import org.example.homebankapp.controller.request.WithdrawRequest;
import org.example.homebankapp.controller.response.AccountResponse;
import org.example.homebankapp.exception.AccountNotFoundException;
import org.example.homebankapp.exception.InsufficientFundsException;
import org.example.homebankapp.exception.UserNotFoundException;
import org.example.homebankapp.model.BankAccount;
import org.example.homebankapp.model.TransactionType;
import org.example.homebankapp.repository.BankAccountRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class BankAccountService {

    private final BankAccountRepository accountRepository;
    private final TransactionService transactionService;
    private final UserService userService;

    public BankAccountService(BankAccountRepository accountRepository,
                              TransactionService transactionService,
                              UserService userService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for userId={}", request.userId());

        if (!userService.userExistsById(request.userId())) {
            throw new UserNotFoundException(request.userId());
        }

        String email = getAuthenticatedEmail();
        String currentUserId = userService.getUserByEmail(email).id();
        if (!request.userId().equals(currentUserId)) {
            log.warn("Access denied: user {} tried to create account for userId={}", email, request.userId());
            throw new AccessDeniedException("You can only create accounts for yourself");
        }

        BankAccount account = new BankAccount();
        account.setUserId(request.userId());
        account.setCurrency(request.currency());
        account.setBalance(request.initialBalance());

        for (int attempt = 1; attempt <= 3; attempt++) {
            account.setAccountNumber(generateAccountNumber());
            try {
                BankAccount saved = accountRepository.save(account);
                log.info("Account created id={}", saved.getId());
                return toResponse(saved);
            } catch (DuplicateKeyException e) {
                log.warn("Account number collision on attempt {}, retrying...", attempt);
            }
        }
        throw new IllegalStateException("Failed to generate a unique account number after 3 attempts");
    }

    public Page<AccountResponse> getAccountsByUserId(String userId, Pageable pageable) {
        return accountRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    public AccountResponse getAccountById(String id) {
        return toResponse(findById(id));
    }

    public AccountResponse deposit(String accountId, DepositRequest request) {
        log.info("Deposit accountId={} amount={}", accountId, request.amount());

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount account = findById(accountId);
        checkOwnership(account);
        account.setBalance(account.getBalance().add(request.amount()));
        BankAccount saved = accountRepository.save(account);

        transactionService.saveTransaction(accountId, null, account.getAccountNumber(), null, TransactionType.DEPOSIT, request.amount());
        return toResponse(saved);
    }

    public AccountResponse withdraw(String accountId, WithdrawRequest request) {
        log.info("Withdraw accountId={} amount={}", accountId, request.amount());

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount account = findById(accountId);
        checkOwnership(account);

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(account.getBalance(), request.amount());
        }

        account.setBalance(account.getBalance().subtract(request.amount()));
        BankAccount saved = accountRepository.save(account);

        transactionService.saveTransaction(accountId, null, account.getAccountNumber(), null, TransactionType.WITHDRAW, request.amount());
        return toResponse(saved);
    }

    public AccountResponse transfer(String fromAccountId, TransferRequest request) {
        log.info("Transfer from accountId={} to accountNumber={} amount={}",
                fromAccountId, request.toAccountNumber(), request.amount());

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount from = findById(fromAccountId);
        checkOwnership(from);

        if (from.getAccountNumber().equals(request.toAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        BankAccount to = accountRepository.findByAccountNumber(request.toAccountNumber())
                .orElseThrow(() -> AccountNotFoundException.byAccountNumber(request.toAccountNumber()));

        if (from.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(from.getBalance(), request.amount());
        }

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));

        accountRepository.save(to);
        BankAccount saved = accountRepository.save(from);

        transactionService.saveTransaction(fromAccountId, to.getId(), from.getAccountNumber(), to.getAccountNumber(), TransactionType.TRANSFER, request.amount());
        return toResponse(saved);
    }

    private void checkOwnership(BankAccount account) {
        String email = getAuthenticatedEmail();
        String currentUserId = userService.getUserByEmail(email).id();
        if (!account.getUserId().equals(currentUserId)) {
            log.warn("Access denied: user {} tried to access account {}", email, account.getId());
            throw new AccessDeniedException("You do not have access to account " + account.getId());
        }
    }

    private String getAuthenticatedEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        return auth.getName();
    }

    private BankAccount findById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private AccountResponse toResponse(BankAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreatedAt()
        );
    }
}
