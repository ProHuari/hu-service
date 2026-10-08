package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CursoRequest;
import com.huariservice.huariia.DTOs.CursoResponse;
import com.huariservice.huariia.entities.Autor;
import com.huariservice.huariia.entities.Categoria;
import com.huariservice.huariia.entities.Curso;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AutorRepository;
import com.huariservice.huariia.repositories.CategoriasRepository;
import com.huariservice.huariia.repositories.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursosService {

    private final CursoRepository cursoRepository;
    private final AutorRepository autorRepository;
    private final CategoriasRepository categoriasRepository;

    public CursosService(CursoRepository cursoRepository, AutorRepository autorRepository, CategoriasRepository categoriasRepository) {
        this.cursoRepository = cursoRepository;
        this.autorRepository = autorRepository;
        this.categoriasRepository = categoriasRepository;
    }

    public CursoResponse pubCurso(CursoRequest request) {
        Curso curso = new Curso();
        curso.setTitulos(request.getTitulos());
        curso.setDescricao(request.getDescricao());
        curso.setUrlVideo(request.getUrlVideo());

        Categoria categoria = categoriasRepository.findById(request.getCategoria().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        curso.setCategoria(categoria);

        Autor autor = autorRepository.findById(request.getAutores().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
        curso.setAutores(autor);

        Curso salvo = cursoRepository.save(curso);
        return new CursoResponse(salvo.getId(), salvo.getTitulos(), salvo.getDescricao(), salvo.getUrlVideo(), salvo.getCategoria(), salvo.getAutores());
    }

    public List<CursoResponse> mostrarCursos() {
        return cursoRepository.findAll().stream().map(curso -> new CursoResponse(
                curso.getId(),
                curso.getTitulos(),
                curso.getDescricao(),
                curso.getUrlVideo(),
                curso.getCategoria(),
                curso.getAutores()
        )).toList();
    }


    public CursoResponse mudarCurso(Long id, CursoRequest cursoAlterado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));

        curso.setTitulos(cursoAlterado.getTitulos());
        curso.setDescricao(cursoAlterado.getDescricao());
        curso.setUrlVideo(cursoAlterado.getUrlVideo());

        Categoria categoria = categoriasRepository.findById(cursoAlterado.getCategoria().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        curso.setCategoria(categoria);

        Autor autor = autorRepository.findById(cursoAlterado.getAutores().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
        curso.setAutores(autor);

        Curso atualizado = cursoRepository.save(curso);
        return new CursoResponse(atualizado.getId(), atualizado.getTitulos(), atualizado.getDescricao(), atualizado.getUrlVideo(), atualizado.getCategoria(), atualizado.getAutores());
    }

    public void deletarId(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));
        cursoRepository.delete(curso);
    }
}
