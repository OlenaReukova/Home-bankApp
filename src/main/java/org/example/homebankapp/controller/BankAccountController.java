package org.example.homebankapp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.homebankapp.controller.request.CreateAccountRequest;
import org.example.homebankapp.controller.request.DepositRequest;
import org.example.homebankapp.controller.request.TransferRequest;
import org.example.homebankapp.controller.request.WithdrawRequest;
import org.example.homebankapp.controller.response.AccountResponse;
import org.example.homebankapp.service.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bankAccountService.createAccount(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AccountResponse>> getAccountsByUserId(
            @PathVariable String userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bankAccountService.getAccountsByUserId(userId, pageable));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable String id,
                                                   @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(bankAccountService.deposit(id, request));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable String id,
                                                    @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(bankAccountService.withdraw(id, request));
    }

    @PostMapping("/{id}/transfer")
    public ResponseEntity<AccountResponse> transfer(@PathVariable String id,
                                                    @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(bankAccountService.transfer(id, request));
    }
}
