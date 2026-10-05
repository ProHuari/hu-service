package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.DTOs.ProgressoAulaResponse;
import com.huariservice.huariia.entities.Aula;
import com.huariservice.huariia.entities.ProgressoAula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AulasRepository;
import com.huariservice.huariia.repositories.ProgressoAulaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public ProgressoAulaResponse pubProgressoAula(ProgressoAulaRequest request) {
        ProgressoAula progressoAula = new ProgressoAula();
        progressoAula.setStatusAula(request.getStatusAula());
        progressoAula.setConclusao(request.getConclusao() != null ? request.getConclusao() : LocalDateTime.now());

        Usuario usuario = usuarioRepository.findById(request.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        progressoAula.setUsuario(usuario);

        Aula aula = aulasRepository.findById(request.getAula().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa aula não foi publicada"));
        progressoAula.setAula(aula);

        ProgressoAula salvo = progressoAulaRepository.save(progressoAula);
        return new ProgressoAulaResponse(salvo.getId(), salvo.getStatusAula(), salvo.getConclusao(), salvo.getUsuario(), salvo.getAula());
    }

    public List<ProgressoAulaResponse> mostrarProgressoAula() {
        return progressoAulaRepository.findAll().stream().map(progresso -> new ProgressoAulaResponse(
                progresso.getId(),
                progresso.getStatusAula(),
                progresso.getConclusao(),
                progresso.getUsuario(),
                progresso.getAula()
        )).toList();
    }
    public ProgressoAulaResponse mudarProgressoAula(Long id, ProgressoAulaRequest progressoAlterado) {
        ProgressoAula progressoAula = progressoAulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse progresso de aula não foi registrado"));

        progressoAula.setStatusAula(progressoAlterado.getStatusAula());
        if (progressoAlterado.getConclusao() != null) {
            progressoAula.setConclusao(progressoAlterado.getConclusao());
        }

        Usuario usuario = usuarioRepository.findById(progressoAlterado.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        progressoAula.setUsuario(usuario);

        Aula aula = aulasRepository.findById(progressoAlterado.getAula().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa aula não foi publicada"));
        progressoAula.setAula(aula);

        ProgressoAula atualizado = progressoAulaRepository.save(progressoAula);
        return new ProgressoAulaResponse(atualizado.getId(), atualizado.getStatusAula(), atualizado.getConclusao(), atualizado.getUsuario(), atualizado.getAula());
    }
    public void deletarId(Long id) {
        ProgressoAula progressoAula = progressoAulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse progresso de aula não foi registrado"));
        progressoAulaRepository.delete(progressoAula);
    }
}
