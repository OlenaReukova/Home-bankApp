package org.example.homebankapp.controller;

import org.example.homebankapp.controller.request.CreateAccountRequest;
import org.example.homebankapp.controller.request.DepositRequest;
import org.example.homebankapp.controller.request.TransferRequest;
import org.example.homebankapp.controller.request.WithdrawRequest;
import org.example.homebankapp.model.Currency;
import org.example.homebankapp.service.BankAccountService;
import org.example.homebankapp.service.TransactionService;
import org.example.homebankapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequestMapping("/dashboard/accounts")
public class AccountWebController {

    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final UserService userService;

    public AccountWebController(BankAccountService bankAccountService,
                                TransactionService transactionService,
                                UserService userService) {
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/new")
    public String newAccountForm(Model model) {
        model.addAttribute("currencies", Currency.values());
        return "account-new";
    }

    @PostMapping
    public String createAccount(@RequestParam Currency currency,
                                @RequestParam BigDecimal initialBalance,
                                Principal principal) {
        String userId = userService.getUserByEmail(principal.getName()).id();
        bankAccountService.createAccount(new CreateAccountRequest(userId, currency, initialBalance));
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String accountDetail(@PathVariable String id, Model model) {
        model.addAttribute("account", bankAccountService.getAccountById(id));
        model.addAttribute("transactions", transactionService.getTransactionsByAccountId(id,
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent());
        model.addAttribute("accountId", id);
        return "account-detail";
    }

    @GetMapping("/{id}/deposit")
    public String depositForm(@PathVariable String id, Model model) {
        model.addAttribute("accountId", id);
        return "account-deposit";
    }

    @PostMapping("/{id}/deposit")
    public String deposit(@PathVariable String id, @RequestParam BigDecimal amount) {
        bankAccountService.deposit(id, new DepositRequest(amount));
        return "redirect:/dashboard/accounts/" + id;
    }

    @GetMapping("/{id}/withdraw")
    public String withdrawForm(@PathVariable String id, Model model) {
        model.addAttribute("accountId", id);
        return "account-withdraw";
    }

    @PostMapping("/{id}/withdraw")
    public String withdraw(@PathVariable String id, @RequestParam BigDecimal amount) {
        bankAccountService.withdraw(id, new WithdrawRequest(amount));
        return "redirect:/dashboard/accounts/" + id;
    }

    @GetMapping("/{id}/transfer")
    public String transferForm(@PathVariable String id, Model model) {
        model.addAttribute("accountId", id);
        return "account-transfer";
    }

    @PostMapping("/{id}/transfer")
    public String transfer(@PathVariable String id,
                           @RequestParam String toAccountNumber,
                           @RequestParam BigDecimal amount) {
        bankAccountService.transfer(id, new TransferRequest(toAccountNumber, amount));
        return "redirect:/dashboard/accounts/" + id;
    }
}
