package org.example.homebankapp.dto;

import java.math.BigDecimal;

public record UserDto(
        String firstName,
        String secondName,
        BigDecimal balance) {}
