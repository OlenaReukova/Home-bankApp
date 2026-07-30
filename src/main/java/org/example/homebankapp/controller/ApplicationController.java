package org.example.homebankapp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.homebankapp.dto.UpdateUserRequest;
import org.example.homebankapp.dto.UserDto;
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
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.create(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<UserDto> partialUpdateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        UserDto updateUser = userService.partialUpdateUser(id, request);

        return ResponseEntity.ok(updateUser);
    }
}
