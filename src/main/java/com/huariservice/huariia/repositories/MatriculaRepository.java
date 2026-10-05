package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);   // matricular só uma vez
    List<Matricula> findByUsuarioId(Long usuarioId);                      // "meus cursos"
}
