package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.ParliamentarianExpense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParliamentarianExpenseResponse(
        Long id,
        Long parliamentarianId,
        String parliamentarianName,
        String expenseCategory,
        String supplierName,
        BigDecimal amount,
        LocalDate expenseDate,
        String documentUrl
) {
    public static ParliamentarianExpenseResponse from(ParliamentarianExpense entity) {
        return new ParliamentarianExpenseResponse(
                entity.getId(),
                entity.getParliamentarian().getId(),
                entity.getParliamentarian().getName(),
                entity.getExpenseCategory(),
                entity.getSupplierName(),
                entity.getAmount(),
                entity.getExpenseDate(),
                entity.getDocumentUrl()
        );
    }
}

