package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CursoRequest;
import com.huariservice.huariia.DTOs.CursoResponse;
import com.huariservice.huariia.entities.Autor;
import com.huariservice.huariia.entities.Categoria;
import com.huariservice.huariia.entities.Curso;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AutorRepository;
import com.huariservice.huariia.repositories.CategoriaRepository;
import com.huariservice.huariia.repositories.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CursoService {

    private final CursoRepository cursoRepository;
    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;

    public CursoService(CursoRepository cursoRepository,
                        AutorRepository autorRepository,
                        CategoriaRepository categoriaRepository) {
        this.cursoRepository = cursoRepository;
        this.autorRepository = autorRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public CursoResponse pubCurso(CursoRequest request) {
        if (cursoRepository.existsByTitulo(request.titulo())) {
            throw new ConflitoException("Já existe um curso com esse título");
        }
        if (cursoRepository.existsByUrlVideo(request.urlVideo())) {
            throw new ConflitoException("Já existe um curso com essa URL de vídeo");
        }

        Curso curso = new Curso();
        preencher(curso, request);

        return CursoResponse.from(cursoRepository.save(curso));
    }

    @Transactional(readOnly = true)
    public List<CursoResponse> mostrarCursos() {
        return cursoRepository.findAll().stream().map(CursoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(Long id) {
        return CursoResponse.from(buscarCurso(id));
    }

    public CursoResponse mudarCurso(Long id, CursoRequest request) {
        Curso curso = buscarCurso(id);

        if (!curso.getTitulo().equals(request.titulo())
                && cursoRepository.existsByTitulo(request.titulo())) {
            throw new ConflitoException("Já existe um curso com esse título");
        }
        if (!curso.getUrlVideo().equals(request.urlVideo())
                && cursoRepository.existsByUrlVideo(request.urlVideo())) {
            throw new ConflitoException("Já existe um curso com essa URL de vídeo");
        }

        preencher(curso, request);

        return CursoResponse.from(cursoRepository.save(curso));
    }

    public void deletarId(Long id) {
        cursoRepository.delete(buscarCurso(id));
    }

    private void preencher(Curso curso, CursoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));

        curso.setTitulo(request.titulo());
        curso.setDescricao(request.descricao());
        curso.setUrlVideo(request.urlVideo());
        curso.setCategoria(categoria);
        curso.setAutor(autor);
    }

    private Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse curso não foi publicado"));
    }
}