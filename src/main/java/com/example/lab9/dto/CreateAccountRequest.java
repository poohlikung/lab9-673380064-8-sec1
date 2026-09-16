package com.example.lab9.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank(message = "accountNumber is required")
        @Size(max = 30, message = "accountNumber must not exceed 30 characters")
        String accountNumber,

        @NotBlank(message = "ownerName is required")
        @Size(max = 100, message = "ownerName must not exceed 100 characters")
        String ownerName,

        @PositiveOrZero(message = "balance must be zero or greater")
        Double balance
) {
}
