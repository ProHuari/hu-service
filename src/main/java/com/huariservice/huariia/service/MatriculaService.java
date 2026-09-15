package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.MatriculasRequest;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Matricula;
import com.huariservice.huariia.entities.Usuario;
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
    public MatriculasRequest pubMatricula(MatriculasRequest request) {
        Matricula matricula = new Matricula();
        matricula.setDtMatricula(request.getDtMatricula());
        matricula.setStatusMT(request.getStatusMT());

        Usuario usuario = usuarioRepository.findById(request.getUsuario().getId()).orElseThrow();
        matricula.setUsuario(usuario);

        Cursos cursos = cursosRepository.findById(request.getCursos().getId()).orElseThrow();
        matricula.setCursos(cursos);

        matriculaRepository.save(matricula);
        return request;
    }
}
