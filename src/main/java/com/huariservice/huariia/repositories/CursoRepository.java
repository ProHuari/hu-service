package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursosRepository extends JpaRepository<Curso, Long> {
    boolean existsByTitulo(String titulo);
    boolean existsByUrlVideo(String urlVideo);
}
