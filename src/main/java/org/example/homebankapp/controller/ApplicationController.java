package org.example.homebankapp.controller;

import lombok.AllArgsConstructor;
import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/bank")
@RestController
@AllArgsConstructor
@Validated
public class ApplicationController {

    private final UserService userService;

    @PostMapping("/user")
    public UserDto createUser(@RequestBody UserDto userDto){
        return userService.create(userDto);
    }
}
