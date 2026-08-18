package org.example.homebankapp.controller.response;

import org.example.homebankapp.model.Role;

public record AdminUserResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role) {
}
