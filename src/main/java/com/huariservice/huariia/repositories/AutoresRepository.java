package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Autores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoresRepository extends JpaRepository<Autores, Long> {
}
