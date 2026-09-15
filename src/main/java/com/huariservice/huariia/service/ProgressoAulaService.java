package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.ProgressoAulaRequest;
import com.huariservice.huariia.entities.Aulas;
import com.huariservice.huariia.entities.ProgressoAula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.repositories.AulasRepository;
import com.huariservice.huariia.repositories.ProgressoAulaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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


}
