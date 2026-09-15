package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.ProgressoAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressoAulaRepository extends JpaRepository<ProgressoAula, Long> {
}
