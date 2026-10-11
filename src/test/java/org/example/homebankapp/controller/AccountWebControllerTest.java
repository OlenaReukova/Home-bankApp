package org.example.homebankapp.controller;

import org.example.homebankapp.config.CustomUserDetailsService;
import org.example.homebankapp.config.SecurityConfig;
import org.example.homebankapp.controller.response.AccountResponse;
import org.example.homebankapp.controller.response.TransactionResponse;
import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.model.Currency;
import org.example.homebankapp.model.TransactionType;
import org.example.homebankapp.service.BankAccountService;
import org.example.homebankapp.service.TransactionService;
import org.example.homebankapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountWebController.class)
@Import(SecurityConfig.class)
class AccountWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankAccountService bankAccountService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private static final String USER_EMAIL = "user@bank.com";
    private static final String USER_ID = "user-1";
    private static final String ACCOUNT_ID = "acc-1";

    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        accountResponse = new AccountResponse(ACCOUNT_ID, USER_ID, "ACC-TESTTEST",
                new BigDecimal("500.00"), Currency.USD, LocalDateTime.now());

        when(userService.getUserByEmail(USER_EMAIL))
                .thenReturn(new UserResponse(USER_ID, "John", "Doe", USER_EMAIL, "+44123456789"));
    }

    // --- GET /dashboard/accounts/new ---

    @Test
    void newAccountForm_shouldReturnView_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/dashboard/accounts/new").with(user(USER_EMAIL).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("account-new"))
                .andExpect(model().attributeExists("currencies"));
    }

    @Test
    void newAccountForm_shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/dashboard/accounts/new").accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // --- POST /dashboard/accounts ---

    @Test
    void createAccount_shouldRedirectToDashboard() throws Exception {
        when(bankAccountService.createAccount(any())).thenReturn(accountResponse);

        mockMvc.perform(post("/dashboard/accounts")
                        .with(user(USER_EMAIL).roles("USER"))
                        .with(csrf())
                        .param("currency", "USD")
                        .param("initialBalance", "500.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(bankAccountService).createAccount(any());
    }

    // --- GET /dashboard/accounts/{id} ---

    @Test
    void accountDetail_shouldReturnViewWithAccountAndTransactions() throws Exception {
        TransactionResponse txn = new TransactionResponse("txn-1", TransactionType.DEPOSIT,
                new BigDecimal("100.00"), "ACC-TESTTEST", null, LocalDateTime.now());

        when(bankAccountService.getAccountById(ACCOUNT_ID)).thenReturn(accountResponse);
        when(transactionService.getTransactionsByAccountId(eq(ACCOUNT_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(txn)));

        mockMvc.perform(get("/dashboard/accounts/{id}", ACCOUNT_ID).with(user(USER_EMAIL).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("account-detail"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attribute("accountId", ACCOUNT_ID));
    }

    // --- GET /dashboard/accounts/{id}/deposit ---

    @Test
    void depositForm_shouldReturnView() throws Exception {
        mockMvc.perform(get("/dashboard/accounts/{id}/deposit", ACCOUNT_ID).with(user(USER_EMAIL).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("account-deposit"))
                .andExpect(model().attribute("accountId", ACCOUNT_ID));
    }

    // --- POST /dashboard/accounts/{id}/deposit ---

    @Test
    void deposit_shouldCallServiceAndRedirect() throws Exception {
        when(bankAccountService.deposit(eq(ACCOUNT_ID), any())).thenReturn(accountResponse);

        mockMvc.perform(post("/dashboard/accounts/{id}/deposit", ACCOUNT_ID)
                        .with(user(USER_EMAIL).roles("USER"))
                        .with(csrf())
                        .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/accounts/" + ACCOUNT_ID));

        verify(bankAccountService).deposit(eq(ACCOUNT_ID), any());
    }

    // --- GET /dashboard/accounts/{id}/withdraw ---

    @Test
    void withdrawForm_shouldReturnView() throws Exception {
        mockMvc.perform(get("/dashboard/accounts/{id}/withdraw", ACCOUNT_ID).with(user(USER_EMAIL).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("account-withdraw"))
                .andExpect(model().attribute("accountId", ACCOUNT_ID));
    }

    // --- POST /dashboard/accounts/{id}/withdraw ---

    @Test
    void withdraw_shouldCallServiceAndRedirect() throws Exception {
        when(bankAccountService.withdraw(eq(ACCOUNT_ID), any())).thenReturn(accountResponse);

        mockMvc.perform(post("/dashboard/accounts/{id}/withdraw", ACCOUNT_ID)
                        .with(user(USER_EMAIL).roles("USER"))
                        .with(csrf())
                        .param("amount", "50.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/accounts/" + ACCOUNT_ID));

        verify(bankAccountService).withdraw(eq(ACCOUNT_ID), any());
    }

    // --- GET /dashboard/accounts/{id}/transfer ---

    @Test
    void transferForm_shouldReturnView() throws Exception {
        mockMvc.perform(get("/dashboard/accounts/{id}/transfer", ACCOUNT_ID).with(user(USER_EMAIL).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("account-transfer"))
                .andExpect(model().attribute("accountId", ACCOUNT_ID));
    }

    // --- POST /dashboard/accounts/{id}/transfer ---

    @Test
    void transfer_shouldCallServiceAndRedirect() throws Exception {
        when(bankAccountService.transfer(eq(ACCOUNT_ID), any())).thenReturn(accountResponse);

        mockMvc.perform(post("/dashboard/accounts/{id}/transfer", ACCOUNT_ID)
                        .with(user(USER_EMAIL).roles("USER"))
                        .with(csrf())
                        .param("toAccountNumber", "ACC-ABCDEF12")
                        .param("amount", "200.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/accounts/" + ACCOUNT_ID));

        verify(bankAccountService).transfer(eq(ACCOUNT_ID), any());
    }
}
