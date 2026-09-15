package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.DTOs.ProgressoAulaResponse;
import com.huariservice.huariia.entities.Aulas;
import com.huariservice.huariia.entities.ProgressoAula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.repositories.AulasRepository;
import com.huariservice.huariia.repositories.ProgressoAulaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressoAulaService {

    private final ProgressoAulaRepository progressoAulaRepository;
    private final AulasRepository aulasRepository;
    private final UsuarioRepository usuarioRepository;

    public ProgressoAulaService(ProgressoAulaRepository progressoAulaRepository, AulasRepository aulasRepository, UsuarioRepository usuarioRepository) {
        this.progressoAulaRepository = progressoAulaRepository;
        this.aulasRepository = aulasRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ProgressoAulaRequest pubProgressoAula(ProgressoAulaRequest request) {
        ProgressoAula progressoAula = new ProgressoAula();
        progressoAula.setStatusAula(request.getStatusAula());
        progressoAula.setConclusao(request.getConclusao() != null ? request.getConclusao() : LocalDateTime.now());

        Usuario usuario = usuarioRepository.findById(request.getUsuario().getId()).orElseThrow();
        progressoAula.setUsuario(usuario);

        Aulas aulas = aulasRepository.findById(request.getAulas().getId()).orElseThrow();
        progressoAula.setAulas(aulas);

        progressoAulaRepository.save(progressoAula);
        return request;
    }

    public List<ProgressoAulaResponse> mostrarProgressoAula() {
        return progressoAulaRepository.findAll().stream().map(progresso -> new ProgressoAulaResponse(
                progresso.getId(),
                progresso.getStatusAula(),
                progresso.getConclusao(),
                progresso.getUsuario(),
                progresso.getAulas()
        )).toList();
    }

    public String mudarProgressoAula(Long id, ProgressoAulaRequest progressoAlterado) {
        ProgressoAula progressoAula = progressoAulaRepository.findById(id).orElseThrow();

        progressoAula.setStatusAula(progressoAlterado.getStatusAula());
        if (progressoAlterado.getConclusao() != null) {
            progressoAula.setConclusao(progressoAlterado.getConclusao());
        }

        Usuario usuario = usuarioRepository.findById(progressoAlterado.getUsuario().getId()).orElseThrow();
        progressoAula.setUsuario(usuario);

        Aulas aulas = aulasRepository.findById(progressoAlterado.getAulas().getId()).orElseThrow();
        progressoAula.setAulas(aulas);

        progressoAulaRepository.save(progressoAula);
        return "Progresso de aula alterado";
    }

    public String deletarId(Long id) {
        Optional<ProgressoAula> progressoAula = progressoAulaRepository.findById(id);

        if (progressoAula.isEmpty()) {
            return "Esse progresso de aula não foi registrado";
        } else {
            progressoAulaRepository.deleteById(id);
            return "Progresso excluído";
        }
    }
}
