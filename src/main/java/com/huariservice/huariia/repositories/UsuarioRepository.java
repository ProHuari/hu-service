package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);      // login
    boolean existsByEmail(String email);              // evita e-mail duplicado no cadastro

}
