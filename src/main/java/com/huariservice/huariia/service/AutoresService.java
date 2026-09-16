package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.AutoresRequest;
import com.huariservice.huariia.DTOs.AutoresResponse;
import com.huariservice.huariia.entities.Autores;
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

    public AutoresResponse pubAutor(AutoresRequest request) {
        Autores autor = new Autores();
        autor.setNomeCanal(request.getNomeCanal());
        autor.setLinkCanal(request.getLinkCanal());

        Autores salvo = autoresRepository.save(autor);
        return new AutoresResponse(salvo.getId(), salvo.getNomeCanal(), salvo.getLinkCanal());
    }

    public List<AutoresResponse> mostrarAutores() {
        return autoresRepository.findAll().stream().map(autor -> new AutoresResponse(
                autor.getId(),
                autor.getNomeCanal(),
                autor.getLinkCanal()
        )).toList();
    }


    public AutoresResponse mudarAutor(Long id, AutoresRequest autorAlterado) {
        Autores autor = autoresRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));

        autor.setNomeCanal(autorAlterado.getNomeCanal());
        autor.setLinkCanal(autorAlterado.getLinkCanal());

        Autores atualizado = autoresRepository.save(autor);
        return new AutoresResponse(atualizado.getId(), atualizado.getNomeCanal(), atualizado.getLinkCanal());
    }
    public void deletarId(Long id) {
        Autores autor = autoresRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
        autoresRepository.delete(autor);
    }
}
