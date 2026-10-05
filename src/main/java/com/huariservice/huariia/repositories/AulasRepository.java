package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulasRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByModuloIdOrderByOrdemAsc(Long moduloId);
    boolean existsByModuloIdAndOrdem(Long moduloId, Integer ordem);
    boolean existsByUrlVideo(String urlVideo);
}
