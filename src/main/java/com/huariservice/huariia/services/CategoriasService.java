package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CategoriasRequest;
import com.huariservice.huariia.DTOs.CategoriasResponse;
import com.huariservice.huariia.entities.Categoria;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CategoriasRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriasService {

    private final CategoriasRepository categoriasRepository;

    public CategoriasService(CategoriasRepository categoriasRepository) {
        this.categoriasRepository = categoriasRepository;
    }

    public CategoriasResponse pubCategoria(CategoriasRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());

        Categoria salva = categoriasRepository.save(categoria);
        return new CategoriasResponse(salva.getId(), salva.getNome(), salva.getDescricao());
    }
    public List<CategoriasResponse> mostrarCategorias() {
        return categoriasRepository.findAll().stream().map(categoria -> new CategoriasResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        )).toList();
    }
    public CategoriasResponse mudarCategoria(Long id, CategoriasRequest categoriaAlterada) {
        Categoria categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));

        categoria.setNome(categoriaAlterada.nome());
        categoria.setDescricao(categoriaAlterada.descricao());

        Categoria atualizada = categoriasRepository.save(categoria);
        return new CategoriasResponse(atualizada.getId(), atualizada.getNome(), atualizada.getDescricao());
    }

    public void deletarId(Long id) {
        Categoria categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        categoriasRepository.delete(categoria);
    }
    public CategoriasResponse buscarPorId(Long id) {
        Categoria categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        return new CategoriasResponse(categoria.getId(), categoria.getNome(), categoria.getDescricao());
    }
}
