package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.CategoriasRequest;
import com.huariservice.huariia.DTOS.CategoriasResponse;
import com.huariservice.huariia.entities.Categorias;
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

    public CategoriasRequest pubCategoria(CategoriasRequest request) {
        Categorias categoria = new Categorias();
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());

        categoriasRepository.save(categoria);
        return request;
    }

    public List<CategoriasResponse> mostrarCategorias() {
        return categoriasRepository.findAll().stream().map(categoria -> new CategoriasResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        )).toList();
    }

    public String mudarCategoria(Long id, CategoriasRequest categoriaAlterada) {
        Categorias categoria = categoriasRepository.findById(id).orElseThrow();

        categoria.setNome(categoriaAlterada.getNome());
        categoria.setDescricao(categoriaAlterada.getDescricao());

        categoriasRepository.save(categoria);
        return "Categoria alterada";
    }

    public String deletarId(Long id) {
        Optional<Categorias> categoria = categoriasRepository.findById(id);

        if (categoria.isEmpty()) {
            return "Essa categoria não foi cadastrada";
        } else {
            categoriasRepository.deleteById(id);
            return "Categoria excluída";
        }
    }
}
