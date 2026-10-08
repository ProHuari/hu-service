package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CategoriaRequest;
import com.huariservice.huariia.DTOs.CategoriaResponse;
import com.huariservice.huariia.entities.Categoria;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriasService {

    private final CategoriaRepository categoriaRepository;

    public CategoriasService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponse pubCategoria(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());

        Categoria salva = categoriaRepository.save(categoria);
        return new CategoriaResponse(salva.getId(), salva.getNome(), salva.getDescricao());
    }
    public List<CategoriaResponse> mostrarCategorias() {
        return categoriaRepository.findAll().stream().map(categoria -> new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        )).toList();
    }
    public CategoriaResponse mudarCategoria(Long id, CategoriaRequest categoriaAlterada) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));

        categoria.setNome(categoriaAlterada.nome());
        categoria.setDescricao(categoriaAlterada.descricao());

        Categoria atualizada = categoriaRepository.save(categoria);
        return new CategoriaResponse(atualizada.getId(), atualizada.getNome(), atualizada.getDescricao());
    }

    public void deletarId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        categoriaRepository.delete(categoria);
    }
    public CategoriaResponse buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getDescricao());
    }
}
