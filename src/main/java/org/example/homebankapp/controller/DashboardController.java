package org.example.homebankapp.controller;

import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.service.BankAccountService;
import org.example.homebankapp.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    public DashboardController(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        UserResponse currentUser = userService.getUserByEmail(email);

        model.addAttribute("firstName", currentUser.firstName());
        model.addAttribute("email", email);
        model.addAttribute("accounts", bankAccountService.getAccountsByUserId(currentUser.id(), Pageable.unpaged()).getContent());
        return "dashboard";
    }
}
