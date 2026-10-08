package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CategoriaRequest;
import com.huariservice.huariia.DTOs.CategoriaResponse;
import com.huariservice.huariia.entities.Categoria;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponse pubCategoria(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());

        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> mostrarCategorias() {
        return categoriaRepository.findAll().stream().map(CategoriaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return CategoriaResponse.from(buscarCategoria(id));
    }

    public CategoriaResponse mudarCategoria(Long id, CategoriaRequest request) {
        Categoria categoria = buscarCategoria(id);
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());

        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    public void deletarId(Long id) {
        categoriaRepository.delete(buscarCategoria(id));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
    }
}