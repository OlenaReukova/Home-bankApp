package org.example.homebankapp.controller;


import jakarta.validation.Valid;
import org.example.homebankapp.controller.request.CreateUserRequest;
import org.example.homebankapp.controller.response.AdminUserResponse;
import org.example.homebankapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUsersController {

    private final UserService userService;

    public AdminUsersController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public String usersPage(Model model) {

        model.addAttribute(
                "users",
                userService.getAllUsersForAdmin()
        );

        return "users";
    }


    @GetMapping("/create")
    public String createPage(Model model) {

        model.addAttribute(
                "user",
                new CreateUserRequest("", "", "", "")
        );

        return "create-user";
    }


    @PostMapping("/create")
    public String createUser(
            @Valid @ModelAttribute("user") CreateUserRequest request,
            BindingResult result
    ) {

        if (result.hasErrors()) {
            return "create-user";
        }

        userService.createUser(request);

        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editPage(
            @PathVariable String id,
            Model model
    ) {
        AdminUserResponse user = userService.getAdminUserById(id);

        model.addAttribute("user", user);

        return "edit-user";
    }

    @PostMapping("/{id}/edit")
    public String updateUser(
            @PathVariable String id,
            @Valid @ModelAttribute("user") CreateUserRequest request,
            BindingResult result
    ) {

        if (result.hasErrors()) {
            return "edit-user";
        }

        userService.updateUser(id, request);

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable String id) {

        userService.deleteUser(id);

        return "redirect:/admin/users";
    }
}
