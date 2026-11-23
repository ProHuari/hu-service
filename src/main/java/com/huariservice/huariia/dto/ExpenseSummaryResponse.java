package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.enums.ParliamentarianChamber;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated analytical summary of parliamentary expenses (CEAP) for dashboard charts.
 */
public record ExpenseSummaryResponse(
        BigDecimal totalAmount,
        long totalExpenses,
        List<ParliamentarianExpenseTotal> rankingByParliamentarian,
        List<CategoryExpenseTotal> distributionByCategory,
        List<StateExpenseTotal> distributionByState
) {

    public record ParliamentarianExpenseTotal(
            Long parliamentarianId,
            String name,
            String party,
            String state,
            ParliamentarianChamber chamber,
            BigDecimal totalAmount,
            long expenseCount
    ) {
    }

    public record CategoryExpenseTotal(
            String category,
            BigDecimal totalAmount,
            long expenseCount
    ) {
    }

    public record StateExpenseTotal(
            String state,
            BigDecimal totalAmount,
            long expenseCount
    ) {
    }
}
