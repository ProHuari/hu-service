package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.ModulosRequest;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Modulo;
import com.huariservice.huariia.repositories.CursosRepository;
import com.huariservice.huariia.repositories.ModuloRepository;
import org.springframework.stereotype.Service;

@Service
public class ModulosService {
    private final ModuloRepository moduloRepository;
    private final CursosRepository cursosRepository;


    public ModulosService(ModuloRepository moduloRepository, CursosRepository cursosRepository) {
        this.moduloRepository = moduloRepository;
        this.cursosRepository = cursosRepository;
    }
    public ModulosRequest pubModulo(ModulosRequest request) {
        Modulo modulo = new Modulo();
        modulo.setTitulo(request.getTitulo());
        modulo.setDescricao(request.getDescricao());
        modulo.setOrdem(request.getOrdem());

        Cursos cursos = cursosRepository.findById(request.getCursos().getId()).orElseThrow();
        modulo.setCursos(cursos);

        moduloRepository.save(modulo);
        return request;
    }
}
