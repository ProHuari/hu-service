package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.ModulosRequest;
import com.huariservice.huariia.DTOS.ModulosResponse;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.entities.Modulo;
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
    public String mudarModulo(Long id, ModulosRequest moduloAlterado) {
        Modulo modulo = moduloRepository.findById(id).orElseThrow();

        modulo.setTitulo(moduloAlterado.getTitulo());
        modulo.setDescricao(moduloAlterado.getDescricao());
        modulo.setOrdem(moduloAlterado.getOrdem());

        Cursos cursos = cursosRepository.findById(moduloAlterado.getCursos().getId()).orElseThrow();
        modulo.setCursos(cursos);

        moduloRepository.save(modulo);
        return "Módulo alterado";
    }
    public String deletarId(Long id) {
        Optional<Modulo> modulo = moduloRepository.findById(id);

        if (modulo.isEmpty()) {
            return "Esse módulo não foi cadastrado";
        } else {
            moduloRepository.deleteById(id);
            return "Módulo excluído";
        }
    }
}
