package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.CategoriasRequest;
import com.huariservice.huariia.DTOs.CategoriasResponse;
import com.huariservice.huariia.entities.Categorias;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CategoriasRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriasService {

    private final CategoriasRepository categoriasRepository;

    public CategoriasService(CategoriasRepository categoriasRepository) {
        this.categoriasRepository = categoriasRepository;
    }

    public CategoriasResponse pubCategoria(CategoriasRequest request) {
        Categorias categoria = new Categorias();
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());

        Categorias salva = categoriasRepository.save(categoria);
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
        Categorias categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));

        categoria.setNome(categoriaAlterada.getNome());
        categoria.setDescricao(categoriaAlterada.getDescricao());

        Categorias atualizada = categoriasRepository.save(categoria);
        return new CategoriasResponse(atualizada.getId(), atualizada.getNome(), atualizada.getDescricao());
    }
    public void deletarId(Long id) {
        Categorias categoria = categoriasRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa categoria não foi cadastrada"));
        categoriasRepository.delete(categoria);
    }
}
