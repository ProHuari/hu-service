package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.MatriculasRequest;
import com.huariservice.huariia.DTOS.MatriculasResponse;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Matricula;
import com.huariservice.huariia.entities.Usuario;
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
    public String mudarMatricula(Long id, MatriculasRequest matriculaAlterada) {
        Matricula matricula = matriculaRepository.findById(id).orElseThrow();

        matricula.setDtMatricula(matriculaAlterada.getDtMatricula());
        matricula.setStatusMT(matriculaAlterada.getStatusMT());

        Usuario usuario = usuarioRepository.findById(matriculaAlterada.getUsuario().getId()).orElseThrow();
        matricula.setUsuario(usuario);

        Cursos cursos = cursosRepository.findById(matriculaAlterada.getCursos().getId()).orElseThrow();
        matricula.setCursos(cursos);

        matriculaRepository.save(matricula);
        return "Matrícula alterada";
    }
    public String deletarId(Long id) {
        Optional<Matricula> matricula = matriculaRepository.findById(id);

        if (matricula.isEmpty()) {
            return "Essa matrícula não foi realizada";
        } else {
            matriculaRepository.deleteById(id);
            return "Matrícula excluída";
        }
    }
}
