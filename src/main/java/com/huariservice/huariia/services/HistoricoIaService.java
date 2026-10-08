package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.HistoricoIaRequest;
import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import com.huariservice.huariia.entities.HistoricoIa;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.HistoricoIaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// O histórico é um registro: não há atualização. dataConsulta é preenchida pela entidade.
@Service
@Transactional
public class HistoricoIaService {

    private final HistoricoIaRepository historicoIaRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoIaService(HistoricoIaRepository historicoIaRepository,
                              UsuarioRepository usuarioRepository) {
        this.historicoIaRepository = historicoIaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public HistoricoIaResponse pubHistoricoIa(HistoricoIaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));

        HistoricoIa historico = new HistoricoIa();
        historico.setPergunta(request.pergunta());
        historico.setResposta(request.resposta());
        historico.setUsuario(usuario);

        return HistoricoIaResponse.from(historicoIaRepository.save(historico));
    }

    @Transactional(readOnly = true)
    public List<HistoricoIaResponse> mostrarHistoricoIa() {
        return historicoIaRepository.findAll().stream().map(HistoricoIaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoIaResponse> mostrarHistoricoDoUsuario(Long usuarioId) {
        return historicoIaRepository.findByUsuarioIdOrderByDataConsultaDesc(usuarioId).stream()
                .map(HistoricoIaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public HistoricoIaResponse buscarPorId(Long id) {
        return HistoricoIaResponse.from(buscarHistorico(id));
    }

    public void deletarId(Long id) {
        historicoIaRepository.delete(buscarHistorico(id));
    }

    private HistoricoIa buscarHistorico(Long id) {
        return historicoIaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse histórico não foi registrado"));
    }
}