package org.example.homebankapp.controller.response;

public record UserResponse(
        String firstName,
        String lastName,
        String email,
        String phoneNumber) {
}
