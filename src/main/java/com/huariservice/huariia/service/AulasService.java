package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.AulasRequest;
import com.huariservice.huariia.DTOs.AulasResponse;
import com.huariservice.huariia.entities.Aulas;
import com.huariservice.huariia.entities.Modulo;
import com.huariservice.huariia.repositories.AulasRepository;
import com.huariservice.huariia.repositories.ModuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AulasService {

    private final AulasRepository aulasRepository;
    private final ModuloRepository moduloRepository;

    public AulasService(AulasRepository aulasRepository, ModuloRepository moduloRepository) {
        this.aulasRepository = aulasRepository;
        this.moduloRepository = moduloRepository;
    }

    public AulasRequest pubAula(AulasRequest request) {
        Aulas aula = new Aulas();
        aula.setTitulos(request.getTitulos());
        aula.setDescricao(request.getDescricao());
        aula.setUrlVideo(request.getUrlVideo());
        aula.setOrdem(request.getOrdem());
        aula.setDuracaoEmMinutos(request.getDuracaoEmMinutos());

        Modulo modulo = moduloRepository.findById(request.getModulo().getId()).orElseThrow();
        aula.setModulo(modulo);

        aulasRepository.save(aula);
        return request;
    }

    public List<AulasResponse> mostrarAulas() {
        return aulasRepository.findAll().stream().map(aula -> new AulasResponse(
                aula.getId(),
                aula.getTitulos(),
                aula.getDescricao(),
                aula.getUrlVideo(),
                aula.getOrdem(),
                aula.getDuracaoEmMinutos(),
                aula.getModulo()
        )).toList();
    }

    public String mudarAula(Long id, AulasRequest aulaAlterada) {
        Aulas aula = aulasRepository.findById(id).orElseThrow();

        aula.setTitulos(aulaAlterada.getTitulos());
        aula.setDescricao(aulaAlterada.getDescricao());
        aula.setUrlVideo(aulaAlterada.getUrlVideo());
        aula.setOrdem(aulaAlterada.getOrdem());
        aula.setDuracaoEmMinutos(aulaAlterada.getDuracaoEmMinutos());

        Modulo modulo = moduloRepository.findById(aulaAlterada.getModulo().getId()).orElseThrow();
        aula.setModulo(modulo);

        aulasRepository.save(aula);
        return "Aula alterada";
    }

    public String deletarId(Long id) {
        Optional<Aulas> aula = aulasRepository.findById(id);

        if (aula.isEmpty()) {
            return "Essa aula não foi publicada";
        } else {
            aulasRepository.deleteById(id);
            return "Aula excluída";
        }
    }
}
