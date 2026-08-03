package org.example.homebankapp.dto;

public record UserResponse(
        String firstName,
        String lastName,
        String email,
        String phoneNumber) {
}
