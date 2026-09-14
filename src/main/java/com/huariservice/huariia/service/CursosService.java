package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.CursosRequest;
import com.huariservice.huariia.DTOS.CursosResponse;
import com.huariservice.huariia.entities.Autores;
import com.huariservice.huariia.entities.Categorias;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.repositories.AutoresRepository;
import com.huariservice.huariia.repositories.CategoriasRepository;
import com.huariservice.huariia.repositories.CursosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public CursosRequest pubCurso(CursosRequest request) {
        Cursos curso = new Cursos();
        curso.setTitulos(request.getTitulos());
        curso.setDescricao(request.getDescricao());
        curso.setUrlVideo(request.getUrlVideo());

        Categorias categoria = categoriasRepository.findById(request.getCategoria().getId()).orElseThrow();
        curso.setCategoria(categoria);

        Autores autores = autoresRepository.findById(request.getAutores().getId()).orElseThrow();
        curso.setAutores(autores);

        cursosRepository.save(curso);
        return request;
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

    public String mudarCurso(Long id, CursosRequest cursoAlterado) {
        Cursos curso = cursosRepository.findById(id).orElseThrow();

        curso.setTitulos(cursoAlterado.getTitulos());
        curso.setDescricao(cursoAlterado.getDescricao());
        curso.setUrlVideo(cursoAlterado.getUrlVideo());

        Categorias categoria = categoriasRepository.findById(cursoAlterado.getCategoria().getId()).orElseThrow();
        curso.setCategoria(categoria);

        Autores autores = autoresRepository.findById(cursoAlterado.getAutores().getId()).orElseThrow();
        curso.setAutores(autores);

        cursosRepository.save(curso);
        return "Curso alterado";
    }

    public String deletarId(Long id) {
        Optional<Cursos> curso = cursosRepository.findById(id);

        if (curso.isEmpty()) {
            return "Esse curso não foi publicado";
        } else {
            cursosRepository.deleteById(id);
            return "Curso excluído";
        }
    }
}
