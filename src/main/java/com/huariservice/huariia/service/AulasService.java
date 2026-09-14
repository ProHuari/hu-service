package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.AulasRequest;
import com.huariservice.huariia.entities.Aulas;
import com.huariservice.huariia.entities.Modulo;
import com.huariservice.huariia.repositories.AulasRepository;
import com.huariservice.huariia.repositories.ModuloRepository;
import org.springframework.stereotype.Service;

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
}
