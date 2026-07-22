package org.example.homebankapp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotNull(message = "Balance is required")
        @DecimalMin(value = "0.01", message = "Balance must be at least 0.01")
        BigDecimal balance) {
}
