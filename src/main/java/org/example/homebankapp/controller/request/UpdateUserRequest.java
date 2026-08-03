package org.example.homebankapp.controller.request;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
