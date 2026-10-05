package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.AutorRequest;
import com.huariservice.huariia.DTOs.AutorResponse;
import com.huariservice.huariia.entities.Autor;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AutoresRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoresService {

    private final AutoresRepository autoresRepository;

    public AutoresService(AutoresRepository autoresRepository) {
        this.autoresRepository = autoresRepository;
    }

    public AutorResponse pubAutor(AutorRequest request) {
        Autor autor = new Autor();
        autor.setNomeCanal(request.nomeCanal());
        autor.setLinkCanal(request.linkCanal());

        Autor salvo = autoresRepository.save(autor);
        return new AutorResponse(salvo.getId(), salvo.getNomeCanal(), salvo.getLinkCanal());
    }

    public List<AutorResponse> mostrarAutores() {
        return autoresRepository.findAll().stream().map(autor -> new AutorResponse(
                autor.getId(),
                autor.getNomeCanal(),
                autor.getLinkCanal()
        )).toList();
    }
    public AutorResponse buscarPorId(Long id) {
        Autor autor = autoresRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
        return new AutorResponse(autor.getId(), autor.getNomeCanal(), autor.getLinkCanal());
    }
    public AutorResponse mudarAutor(Long id, AutorRequest autorAlterado) {
        Autor autor = autoresRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));

        autor.setNomeCanal(autorAlterado.nomeCanal());
        autor.setLinkCanal(autorAlterado.linkCanal());

        Autor atualizado = autoresRepository.save(autor);
        return new AutorResponse(atualizado.getId(), atualizado.getNomeCanal(), atualizado.getLinkCanal());
    }
    public void deletarId(Long id) {
        Autor autor = autoresRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
        autoresRepository.delete(autor);
    }
}
