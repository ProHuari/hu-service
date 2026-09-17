package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.ModulosRequest;
import com.huariservice.huariia.DTOs.ModulosResponse;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Modulo;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CursosRepository;
import com.huariservice.huariia.repositories.ModuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public List<ModulosResponse> mostrarModulos() {
        return moduloRepository.findAll().stream().map(modulo -> new ModulosResponse(
                modulo.getId(),
                modulo.getTitulo(),
                modulo.getDescricao(),
                modulo.getOrdem(),
                modulo.getCursos()
        )).toList();
    }
    public ModulosResponse mudarModulo(Long id, ModulosRequest moduloAlterado) {
        Modulo modulo = moduloRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse módulo não foi cadastrado"));

        modulo.setTitulo(moduloAlterado.getTitulo());
        modulo.setDescricao(moduloAlterado.getDescricao());
        modulo.setOrdem(moduloAlterado.getOrdem());

        Cursos cursos = cursosRepository.findById(moduloAlterado.getCursos().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));
        modulo.setCursos(cursos);

        Modulo atualizado = moduloRepository.save(modulo);
        return new ModulosResponse(atualizado.getId(), atualizado.getTitulo(), atualizado.getDescricao(), atualizado.getOrdem(), atualizado.getCursos());
    }
    public void deletarId(Long id) {
        Modulo modulo = moduloRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse módulo não foi cadastrado"));
        moduloRepository.delete(modulo);
    }
}
