package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.DTOs.ProgressoAulaResponse;
import com.huariservice.huariia.entities.Aula;
import com.huariservice.huariia.entities.ProgressoAula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusAula;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.exceptions.RegraNegocioException;
import com.huariservice.huariia.repositories.AulaRepository;
import com.huariservice.huariia.repositories.MatriculaRepository;
import com.huariservice.huariia.repositories.ProgressoAulaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProgressoAulaService {

    private final ProgressoAulaRepository progressoAulaRepository;
    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;

    public ProgressoAulaService(ProgressoAulaRepository progressoAulaRepository,
                                AulaRepository aulaRepository,
                                UsuarioRepository usuarioRepository,
                                MatriculaRepository matriculaRepository) {
        this.progressoAulaRepository = progressoAulaRepository;
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public ProgressoAulaResponse pubProgressoAula(ProgressoAulaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        Aula aula = aulaRepository.findById(request.aulaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa aula não foi publicada"));

        Long cursoId = aula.getModulo().getCurso().getId();
        if (!matriculaRepository.existsByUsuarioIdAndCursoId(usuario.getId(), cursoId)) {
            throw new RegraNegocioException("O usuário não está matriculado no curso dessa aula");
        }
        if (progressoAulaRepository.findByUsuarioIdAndAulaId(usuario.getId(), aula.getId()).isPresent()) {
            throw new ConflitoException("Já existe progresso registrado para essa aula; atualize o existente");
        }

        ProgressoAula progresso = new ProgressoAula();
        progresso.setUsuario(usuario);
        progresso.setAula(aula);
        aplicarStatus(progresso, request.statusAula());

        return ProgressoAulaResponse.from(progressoAulaRepository.save(progresso));
    }

    @Transactional(readOnly = true)
    public List<ProgressoAulaResponse> mostrarProgressoAula() {
        return progressoAulaRepository.findAll().stream().map(ProgressoAulaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProgressoAulaResponse> mostrarProgressoDoUsuario(Long usuarioId) {
        return progressoAulaRepository.findByUsuarioId(usuarioId).stream()
                .map(ProgressoAulaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProgressoAulaResponse buscarPorId(Long id) {
        return ProgressoAulaResponse.from(buscarProgresso(id));
    }

    // Só o status muda; usuário e aula do registro não podem ser trocados.
    public ProgressoAulaResponse mudarProgressoAula(Long id, ProgressoAulaRequest request) {
        ProgressoAula progresso = buscarProgresso(id);
        aplicarStatus(progresso, request.statusAula());

        return ProgressoAulaResponse.from(progressoAulaRepository.save(progresso));
    }

    public void deletarId(Long id) {
        progressoAulaRepository.delete(buscarProgresso(id));
    }

    // dataConclusao só existe quando a aula está CONCLUIDA.
    private void aplicarStatus(ProgressoAula progresso, StatusAula status) {
        progresso.setStatusAula(status);
        if (status == StatusAula.CONCLUIDA) {
            if (progresso.getDataConclusao() == null) {
                progresso.setDataConclusao(LocalDateTime.now());
            }
        } else {
            progresso.setDataConclusao(null);
        }
    }

    private ProgressoAula buscarProgresso(Long id) {
        return progressoAulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse progresso de aula não foi registrado"));
    }
}