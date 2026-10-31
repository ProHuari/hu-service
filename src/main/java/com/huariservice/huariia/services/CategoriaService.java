package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.CategoriaRequest;
import com.huariservice.huariia.dto.CategoriaResponse;
import com.huariservice.huariia.entities.Categoria;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;


    public CategoriaResponse pubCategoria(CategoriaRequest request) {
        if (categoriaRepository.existsByNome(request.nome())) {
            throw new ConflitoException("Já existe uma categoria com esse nome");
        }

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

        if (!categoria.getNome().equals(request.nome())
                && categoriaRepository.existsByNome(request.nome())) {
            throw new ConflitoException("Já existe uma categoria com esse nome");
        }

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
