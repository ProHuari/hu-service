package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.LegislativeBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LegislativeBillRepository extends JpaRepository<LegislativeBill, Long> {

    Optional<LegislativeBill> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    List<LegislativeBill> findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrCodeContainingIgnoreCase(
            String title, String summary, String code);
}
