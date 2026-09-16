package com.example.lab9.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositRequest(
        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        Double amount
) {
}
