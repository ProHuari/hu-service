package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.ParliamentarianExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParliamentarianExpenseRepository extends JpaRepository<ParliamentarianExpense, Long> {

    List<ParliamentarianExpense> findByParliamentarianIdOrderByExpenseDateDesc(Long parliamentarianId);
}

