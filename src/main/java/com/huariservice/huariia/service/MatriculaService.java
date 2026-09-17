package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.MatriculasRequest;
import com.huariservice.huariia.DTOs.MatriculasResponse;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Matricula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CursosRepository;
import com.huariservice.huariia.repositories.MatriculaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<MatriculasResponse> mostrarMatriculas() {
        return matriculaRepository.findAll().stream().map(matricula -> new MatriculasResponse(
                matricula.getId(),
                matricula.getDtMatricula(),
                matricula.getStatusMT(),
                matricula.getUsuario(),
                matricula.getCursos())).toList();
    }
    public MatriculasResponse mudarMatricula(Long id, MatriculasRequest matriculaAlterada) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa matrícula não foi realizada"));

        matricula.setDtMatricula(matriculaAlterada.getDtMatricula());
        matricula.setStatusMT(matriculaAlterada.getStatusMT());

        Usuario usuario = usuarioRepository.findById(matriculaAlterada.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        matricula.setUsuario(usuario);

        Cursos cursos = cursosRepository.findById(matriculaAlterada.getCursos().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));
        matricula.setCursos(cursos);

        Matricula atualizada = matriculaRepository.save(matricula);
        return new MatriculasResponse(atualizada.getId(), atualizada.getDtMatricula(), atualizada.getStatusMT(), atualizada.getUsuario(), atualizada.getCursos());
    }
    public void deletarId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa matrícula não foi realizada"));
        matriculaRepository.delete(matricula);
    }
}
