package org.example.homebankapp.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
        ) {}
