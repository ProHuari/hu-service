package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.Historico_IaRequest;
import com.huariservice.huariia.DTOs.Historico_IaResponse;
import com.huariservice.huariia.entities.HistoricoIa;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.repositories.HistoricoIaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoIaService {

    private final HistoricoIaRepository historicoIaRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoIaService(HistoricoIaRepository historicoIaRepository, UsuarioRepository usuarioRepository) {
        this.historicoIaRepository = historicoIaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Historico_IaRequest pubHistoricoIa(Historico_IaRequest request) {
        HistoricoIa historico = new HistoricoIa();
        historico.setPergunta(request.getPergunta());
        historico.setResposta(request.getResposta());
        historico.setDataConsulta(LocalDateTime.now());

        Usuario usuario = usuarioRepository.findById(request.getUsuario().getId()).orElseThrow();
        historico.setUsuario(usuario);

        historicoIaRepository.save(historico);
        return request;
    }

    public List<Historico_IaResponse> mostrarHistoricoIa() {
        return historicoIaRepository.findAll().stream().map(historico -> new Historico_IaResponse(
                historico.getId(),
                historico.getDataConsulta(),
                historico.getUsuario()
        )).toList();
    }

    public String mudarHistoricoIa(Long id, Historico_IaRequest historicoAlterado) {
        HistoricoIa historico = historicoIaRepository.findById(id).orElseThrow();

        historico.setPergunta(historicoAlterado.getPergunta());
        historico.setResposta(historicoAlterado.getResposta());

        Usuario usuario = usuarioRepository.findById(historicoAlterado.getUsuario().getId()).orElseThrow();
        historico.setUsuario(usuario);

        historicoIaRepository.save(historico);
        return "Histórico de IA alterado";
    }

    public String deletarId(Long id) {
        Optional<HistoricoIa> historico = historicoIaRepository.findById(id);

        if (historico.isEmpty()) {
            return "Esse histórico não foi registrado";
        } else {
            historicoIaRepository.deleteById(id);
            return "Histórico excluído";
        }
    }
}
