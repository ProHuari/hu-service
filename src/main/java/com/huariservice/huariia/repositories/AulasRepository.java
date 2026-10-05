package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AulasRepository extends JpaRepository<Aula, Long> {
}
