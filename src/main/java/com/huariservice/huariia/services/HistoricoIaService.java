package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.HistoricoIARequest;
import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import com.huariservice.huariia.entities.HistoricoIa;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.HistoricoIaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoIaService {

    private final HistoricoIaRepository historicoIaRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoIaService(HistoricoIaRepository historicoIaRepository, UsuarioRepository usuarioRepository) {
        this.historicoIaRepository = historicoIaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public HistoricoIaResponse pubHistoricoIa(HistoricoIARequest request) {
        HistoricoIa historico = new HistoricoIa();
        historico.setPergunta(request.getPergunta());
        historico.setResposta(request.getResposta());
        historico.setDataConsulta(LocalDateTime.now());

        Usuario usuario = usuarioRepository.findById(request.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        historico.setUsuario(usuario);

        HistoricoIa salvo = historicoIaRepository.save(historico);
        return new HistoricoIaResponse(salvo.getId(), salvo.getDataConsulta(), salvo.getUsuario());
    }

    public List<HistoricoIaResponse> mostrarHistoricoIa() {
        return historicoIaRepository.findAll().stream().map(historico -> new HistoricoIaResponse(
                historico.getId(),
                historico.getDataConsulta(),
                historico.getUsuario()
        )).toList();
    }

    public HistoricoIaResponse mudarHistoricoIa(Long id, HistoricoIARequest historicoAlterado) {
        HistoricoIa historico = historicoIaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse histórico não foi registrado"));

        historico.setPergunta(historicoAlterado.getPergunta());
        historico.setResposta(historicoAlterado.getResposta());

        Usuario usuario = usuarioRepository.findById(historicoAlterado.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
        historico.setUsuario(usuario);

        HistoricoIa atualizado = historicoIaRepository.save(historico);
        return new HistoricoIaResponse(atualizado.getId(), atualizado.getDataConsulta(), atualizado.getUsuario());
    }
    public void deletarId(Long id) {
        HistoricoIa historico = historicoIaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse histórico não foi registrado"));
        historicoIaRepository.delete(historico);
    }
}
