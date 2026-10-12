package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.MatriculaRequest;
import com.huariservice.huariia.DTOs.MatriculaResponse;
import com.huariservice.huariia.entities.Curso;
import com.huariservice.huariia.entities.Matricula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusMatricula;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CursoRepository;
import com.huariservice.huariia.repositories.MatriculaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            CursoRepository cursoRepository,
                            UsuarioRepository usuarioRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // O usuarioId vem do token, nunca do corpo da requisição.
    public MatriculaResponse pubMatricula(Long usuarioId, MatriculaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));

        if (matriculaRepository.existsByUsuarioIdAndCursoId(usuario.getId(), curso.getId())) {
            throw new ConflitoException("O usuário já está matriculado nesse curso");
        }

        Matricula matricula = new Matricula();
        matricula.setUsuario(usuario);
        matricula.setCurso(curso);

        return MatriculaResponse.from(matriculaRepository.save(matricula));
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> mostrarMatriculas() {
        return matriculaRepository.findAll().stream().map(MatriculaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> mostrarMatriculasDoUsuario(Long usuarioId) {
        return matriculaRepository.findByUsuarioId(usuarioId).stream()
                .map(MatriculaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MatriculaResponse buscarPorId(Long id, Long usuarioId, boolean admin) {
        Matricula matricula = buscarMatricula(id);

        // Aluno só enxerga as próprias. Devolve 404 (e não 403) para não revelar que o id existe.
        if (!admin && !matricula.getUsuario().getId().equals(usuarioId)) {
            throw new RecursoNaoEncontradoException("Essa matrícula não foi realizada");
        }
        return MatriculaResponse.from(matricula);
    }

    // Substitui o antigo mudarMatricula: usuário e curso não mudam depois de matriculado.
    public MatriculaResponse mudarStatusMatricula(Long id, StatusMatricula novoStatus) {
        Matricula matricula = buscarMatricula(id);
        matricula.setStatusMatricula(novoStatus);

        return MatriculaResponse.from(matriculaRepository.save(matricula));
    }

    public void deletarId(Long id) {
        matriculaRepository.delete(buscarMatricula(id));
    }

    private Matricula buscarMatricula(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa matrícula não foi realizada"));
    }
}