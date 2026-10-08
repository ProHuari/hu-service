package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.ModuloRequest;
import com.huariservice.huariia.DTOs.ModuloResponse;
import com.huariservice.huariia.entities.Curso;
import com.huariservice.huariia.entities.Modulo;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CursoRepository;
import com.huariservice.huariia.repositories.ModuloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ModuloService {

    private final ModuloRepository moduloRepository;
    private final CursoRepository cursoRepository;

    public ModuloService(ModuloRepository moduloRepository, CursoRepository cursoRepository) {
        this.moduloRepository = moduloRepository;
        this.cursoRepository = cursoRepository;
    }

    public ModuloResponse pubModulo(ModuloRequest request) {
        if (moduloRepository.existsByCursoIdAndOrdem(request.cursoId(), request.ordem())) {
            throw new ConflitoException("Já existe um módulo com essa ordem no curso");
        }

        Modulo modulo = new Modulo();
        preencher(modulo, request);

        return ModuloResponse.from(moduloRepository.save(modulo));
    }

    @Transactional(readOnly = true)
    public List<ModuloResponse> mostrarModulos() {
        return moduloRepository.findAll().stream().map(ModuloResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ModuloResponse> mostrarModulosDoCurso(Long cursoId) {
        return moduloRepository.findByCursoIdOrderByOrdemAsc(cursoId).stream()
                .map(ModuloResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ModuloResponse buscarPorId(Long id) {
        return ModuloResponse.from(buscarModulo(id));
    }

    public ModuloResponse mudarModulo(Long id, ModuloRequest request) {
        Modulo modulo = buscarModulo(id);

        boolean mudouPosicao = !modulo.getCurso().getId().equals(request.cursoId())
                || !modulo.getOrdem().equals(request.ordem());
        if (mudouPosicao && moduloRepository.existsByCursoIdAndOrdem(request.cursoId(), request.ordem())) {
            throw new ConflitoException("Já existe um módulo com essa ordem no curso");
        }

        preencher(modulo, request);

        return ModuloResponse.from(moduloRepository.save(modulo));
    }

    public void deletarId(Long id) {
        moduloRepository.delete(buscarModulo(id));
    }

    private void preencher(Modulo modulo, ModuloRequest request) {
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));

        modulo.setTitulo(request.titulo());
        modulo.setDescricao(request.descricao());
        modulo.setOrdem(request.ordem());
        modulo.setCurso(curso);
    }

    private Modulo buscarModulo(Long id) {
        return moduloRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse módulo não foi cadastrado"));
    }
}