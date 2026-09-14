package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.CursosRequest;
import com.huariservice.huariia.entities.Autores;
import com.huariservice.huariia.entities.Categorias;
import com.huariservice.huariia.entities.Cursos;
import com.huariservice.huariia.repositories.AutoresRepository;
import com.huariservice.huariia.repositories.CategoriasRepository;
import com.huariservice.huariia.repositories.CursosRepository;
import org.springframework.stereotype.Service;

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
}
