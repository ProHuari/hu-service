package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.AulasRequest;
import com.huariservice.huariia.DTOs.AulaResponse;
import com.huariservice.huariia.entities.Aula;
import com.huariservice.huariia.entities.Modulo;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AulaRepository;
import com.huariservice.huariia.repositories.ModuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AulasService {

    private final AulaRepository aulaRepository;
    private final ModuloRepository moduloRepository;

    public AulasService(AulaRepository aulaRepository, ModuloRepository moduloRepository) {
        this.aulaRepository = aulaRepository;
        this.moduloRepository = moduloRepository;
    }

    public AulaResponse pubAula(AulasRequest request) {
        Aula aula = new Aula();
        aula.setTitulos(request.getTitulos());
        aula.setDescricao(request.getDescricao());
        aula.setUrlVideo(request.getUrlVideo());
        aula.setOrdem(request.getOrdem());
        aula.setDuracaoEmMinutos(request.getDuracaoEmMinutos());

        Modulo modulo = moduloRepository.findById(request.getModulo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse módulo não foi cadastrado"));
        aula.setModulo(modulo);

        Aula salva = aulaRepository.save(aula);
        return new AulaResponse(salva.getId(), salva.getTitulos(), salva.getDescricao(), salva.getUrlVideo(), salva.getOrdem(), salva.getDuracaoEmMinutos(), salva.getModulo());
    }

    public List<AulaResponse> mostrarAulas() {
        return aulaRepository.findAll().stream().map(aula -> new AulaResponse(
                aula.getId(),
                aula.getTitulos(),
                aula.getDescricao(),
                aula.getUrlVideo(),
                aula.getOrdem(),
                aula.getDuracaoEmMinutos(),
                aula.getModulo()
        )).toList();
    }

    public AulaResponse mudarAula(Long id, AulasRequest aulaAlterada) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa aula não foi publicada"));

        aula.setTitulos(aulaAlterada.getTitulos());
        aula.setDescricao(aulaAlterada.getDescricao());
        aula.setUrlVideo(aulaAlterada.getUrlVideo());
        aula.setOrdem(aulaAlterada.getOrdem());
        aula.setDuracaoEmMinutos(aulaAlterada.getDuracaoEmMinutos());

        Modulo modulo = moduloRepository.findById(aulaAlterada.getModulo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse módulo não foi cadastrado"));
        aula.setModulo(modulo);

        Aula atualizada = aulaRepository.save(aula);
        return new AulaResponse(atualizada.getId(), atualizada.getTitulos(), atualizada.getDescricao(), atualizada.getUrlVideo(), atualizada.getOrdem(), atualizada.getDuracaoEmMinutos(), atualizada.getModulo());
    }
    public void deletarId(Long id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa aula não foi publicada"));
        aulaRepository.delete(aula);
    }
}
