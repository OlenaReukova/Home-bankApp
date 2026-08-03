package org.example.homebankapp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.homebankapp.controller.request.CreateUserRequest;
import org.example.homebankapp.controller.request.UpdateUserRequest;
import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/bank")
@RestController
@AllArgsConstructor
@Validated
public class ApplicationController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserResponse createdUserResponse = userService.createUser(createUserRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUserResponse);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, @Valid @RequestBody CreateUserRequest dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserResponse> partialUpdateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        UserResponse updateUser = userService.partialUpdateUser(id, request);

        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
