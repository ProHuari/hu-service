package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.ProgressoAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressoAulaRepository extends JpaRepository<ProgressoAula, Long> {
    Optional<ProgressoAula> findByUsuarioIdAndAulaId(Long usuarioId, Long aulaId);
    List<ProgressoAula> findByUsuarioId(Long usuarioId);
}

