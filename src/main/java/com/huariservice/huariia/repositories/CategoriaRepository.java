package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriasRepository extends JpaRepository<Categoria, Long> {
}
