package com.huariservice.huariia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParliamentarianExpenseRequest(
        @NotNull(message = "Parliamentarian ID is required.")
        Long parliamentarianId,
        @NotBlank(message = "Expense category is required.")
        @Size(max = 150)
        String expenseCategory,
        @NotBlank(message = "Supplier name is required.")
        @Size(max = 200)
        String supplierName,
        @NotNull(message = "Amount is required.")
        @DecimalMin(value = "0.01", message = "Amount must be positive.")
        BigDecimal amount,
        @NotNull(message = "Expense date is required.")
        LocalDate expenseDate,
        @Size(max = 254)
        String documentUrl
) {}

