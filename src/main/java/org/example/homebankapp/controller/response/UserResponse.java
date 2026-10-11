package org.example.homebankapp.controller.response;

public record UserResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber) {
}
