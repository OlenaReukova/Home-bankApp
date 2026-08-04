package org.example.homebankapp.controller.response;

public record AdminUserResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber) {
}
