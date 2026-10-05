package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    List<Modulo> findByCursoIdOrderByOrdemAsc(Long cursoId);
    boolean existsByCursoIdAndOrdem(Long cursoId, Integer ordem);
}

