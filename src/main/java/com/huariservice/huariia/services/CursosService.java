package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CursosRequest;
import com.huariservice.huariia.DTOs.CursosResponse;
import com.huariservice.huariia.entities.Autores;
import com.huariservice.huariia.entities.Categorias;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AutoresRepository;
import com.huariservice.huariia.repositories.CategoriasRepository;
import com.huariservice.huariia.repositories.CursosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursosService {

    private final CursosRepository cursosRepository;
    private final AutoresRepository autoresRepository;
    private final CategoriasRepository categoriasRepository;

    public CursosService(CursosRepository cursosRepository, AutoresRepository autoresRepository, CategoriasRepository categoriasRepository) {
        this.cursosRepository = cursosRepository;
        this.autoresRepository = autoresRepository;
        this.categoriasRepository = categoriasRepository;
    }

    public CursosResponse pubCurso(CursosRequest request) {
        Cursos curso = new Cursos();
        curso.setTitulos(request.getTitulos());
        curso.setDescricao(request.getDescricao());
        curso.setUrlVideo(request.getUrlVideo());

        Categorias categoria = categoriasRepository.findById(request.getCategoria().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        curso.setCategoria(categoria);

        Autores autores = autoresRepository.findById(request.getAutores().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
        curso.setAutores(autores);

        Cursos salvo = cursosRepository.save(curso);
        return new CursosResponse(salvo.getId(), salvo.getTitulos(), salvo.getDescricao(), salvo.getUrlVideo(), salvo.getCategoria(), salvo.getAutores());
    }

    public List<CursosResponse> mostrarCursos() {
        return cursosRepository.findAll().stream().map(curso -> new CursosResponse(
                curso.getId(),
                curso.getTitulos(),
                curso.getDescricao(),
                curso.getUrlVideo(),
                curso.getCategoria(),
                curso.getAutores()
        )).toList();
    }
    public CursosResponse mudarCurso(Long id, CursosRequest cursoAlterado) {
        Cursos curso = cursosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));

        curso.setTitulos(cursoAlterado.getTitulos());
        curso.setDescricao(cursoAlterado.getDescricao());
        curso.setUrlVideo(cursoAlterado.getUrlVideo());

        Categorias categoria = categoriasRepository.findById(cursoAlterado.getCategoria().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        curso.setCategoria(categoria);

        Autores autores = autoresRepository.findById(cursoAlterado.getAutores().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
        curso.setAutores(autores);

        Cursos atualizado = cursosRepository.save(curso);
        return new CursosResponse(atualizado.getId(), atualizado.getTitulos(), atualizado.getDescricao(), atualizado.getUrlVideo(), atualizado.getCategoria(), atualizado.getAutores());
    }

    public void deletarId(Long id) {
        Cursos curso = cursosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));
        cursosRepository.delete(curso);
    }
}
