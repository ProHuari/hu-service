package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.Historico_IaRequest;
import com.huariservice.huariia.entities.HistoricoIa;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.repositories.HistoricoIaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
