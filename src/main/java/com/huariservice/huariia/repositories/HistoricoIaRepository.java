package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.HistoricoIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoIaRepository extends JpaRepository<HistoricoIa, Long> {
    List<HistoricoIa> findByUsuarioIdOrderByDataConsultaDesc(Long usuarioId);
}

