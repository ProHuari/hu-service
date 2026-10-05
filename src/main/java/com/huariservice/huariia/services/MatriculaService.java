package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.MatriculaRequest;
import com.huariservice.huariia.DTOs.MatriculaResponse;
import com.huariservice.huariia.entities.Curso;
import com.huariservice.huariia.entities.Matricula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CursosRepository;
import com.huariservice.huariia.repositories.MatriculaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public MatriculaResponse pubMatricula(MatriculaRequest request) {
        Matricula matricula = new Matricula();
        matricula.setDtMatricula(request.getDtMatricula());
        matricula.setStatusMT(request.getStatusMatricula());

        Usuario usuario = usuarioRepository.findById(request.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        matricula.setUsuario(usuario);

        Curso curso = cursosRepository.findById(request.getCursos().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));
        matricula.setCursos(curso);

        Matricula salva = matriculaRepository.save(matricula);
        return new MatriculaResponse(salva.getId(), salva.getDtMatricula(), salva.getStatusMT(), salva.getUsuario(), salva.getCursos());
    }

    public List<MatriculaResponse> mostrarMatriculas() {
        return matriculaRepository.findAll().stream().map(matricula -> new MatriculaResponse(
                matricula.getId(),
                matricula.getDtMatricula(),
                matricula.getStatusMT(),
                matricula.getUsuario(),
                matricula.getCursos())).toList();
    }
    public MatriculaResponse mudarMatricula(Long id, MatriculaRequest matriculaAlterada) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa matrícula não foi realizada"));

        matricula.setDtMatricula(matriculaAlterada.getDtMatricula());
        matricula.setStatusMT(matriculaAlterada.getStatusMatricula());

        Usuario usuario = usuarioRepository.findById(matriculaAlterada.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        matricula.setUsuario(usuario);

        Curso curso = cursosRepository.findById(matriculaAlterada.getCursos().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));
        matricula.setCursos(curso);

        Matricula atualizada = matriculaRepository.save(matricula);
        return new MatriculaResponse(atualizada.getId(), atualizada.getDtMatricula(), atualizada.getStatusMT(), atualizada.getUsuario(), atualizada.getCursos());
    }
    public void deletarId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa matrícula não foi realizada"));
        matriculaRepository.delete(matricula);
    }
}
