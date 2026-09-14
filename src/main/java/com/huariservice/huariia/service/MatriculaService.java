package com.huariservice.huariia.service;

import com.huariservice.huariia.repositories.CursosRepository;
import com.huariservice.huariia.repositories.MatriculaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final CursosRepository cursosRepository;
    private final UsuarioRepository usuarioRepository;

    public MatriculaService(MatriculaRepository matriculaRepository, CursosRepository cursosRepository, UsuarioRepository usuarioRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursosRepository = cursosRepository;
        this.usuarioRepository = usuarioRepository;
    }
}
