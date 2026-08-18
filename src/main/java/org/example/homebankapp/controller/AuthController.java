package org.example.homebankapp.controller;

import org.example.homebankapp.controller.request.RegisterRequest;
import org.example.homebankapp.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
private final AuthService authService;

public AuthController(AuthService authService){
    this.authService=authService;
}

@GetMapping("/register")
    public String registerPage(Model model) {
    model.addAttribute("user", new RegisterRequest("","","","",""));
    return "register";
}

@PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterRequest request){
    authService.register(request);
    return "redirect:/login?registered";
}
}
