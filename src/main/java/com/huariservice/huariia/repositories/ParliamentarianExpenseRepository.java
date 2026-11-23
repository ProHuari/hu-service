package com.huariservice.huariia.repositories;

import com.huariservice.huariia.dto.ExpenseSummaryResponse.CategoryExpenseTotal;
import com.huariservice.huariia.dto.ExpenseSummaryResponse.ParliamentarianExpenseTotal;
import com.huariservice.huariia.dto.ExpenseSummaryResponse.StateExpenseTotal;
import com.huariservice.huariia.entities.ParliamentarianExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ParliamentarianExpenseRepository extends JpaRepository<ParliamentarianExpense, Long> {

    List<ParliamentarianExpense> findByParliamentarianIdOrderByExpenseDateDesc(Long parliamentarianId);

    Page<ParliamentarianExpense> findByParliamentarianIdOrderByExpenseDateDesc(Long parliamentarianId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ParliamentarianExpense e")
    BigDecimal sumTotalAmount();

    @Query("""
            SELECT new com.huariservice.huariia.dto.ExpenseSummaryResponse$ParliamentarianExpenseTotal(
                p.id, p.name, p.party, p.state, p.chamber, COALESCE(SUM(e.amount), 0), COUNT(e)
            )
            FROM ParliamentarianExpense e
            JOIN e.parliamentarian p
            GROUP BY p.id, p.name, p.party, p.state, p.chamber
            ORDER BY SUM(e.amount) DESC
            """)
    List<ParliamentarianExpenseTotal> findRankingByParliamentarian();

    @Query("""
            SELECT new com.huariservice.huariia.dto.ExpenseSummaryResponse$CategoryExpenseTotal(
                e.expenseCategory, COALESCE(SUM(e.amount), 0), COUNT(e)
            )
            FROM ParliamentarianExpense e
            GROUP BY e.expenseCategory
            ORDER BY SUM(e.amount) DESC
            """)
    List<CategoryExpenseTotal> findDistributionByCategory();

    @Query("""
            SELECT new com.huariservice.huariia.dto.ExpenseSummaryResponse$StateExpenseTotal(
                p.state, COALESCE(SUM(e.amount), 0), COUNT(e)
            )
            FROM ParliamentarianExpense e
            JOIN e.parliamentarian p
            GROUP BY p.state
            ORDER BY SUM(e.amount) DESC
            """)
    List<StateExpenseTotal> findDistributionByState();
}
