package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.AutoresRequest;
import com.huariservice.huariia.DTOs.AutoresResponse;
import com.huariservice.huariia.entities.Autores;
import com.huariservice.huariia.repositories.AutoresRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutoresService {

    private final AutoresRepository autoresRepository;

    public AutoresService(AutoresRepository autoresRepository) {
        this.autoresRepository = autoresRepository;
    }

    public AutoresRequest pubAutor(AutoresRequest request) {
        Autores autor = new Autores();
        autor.setNomeCanal(request.getNomeCanal());
        autor.setLinkCanal(request.getLinkCanal());

        autoresRepository.save(autor);
        return request;
    }

    public List<AutoresResponse> mostrarAutores() {
        return autoresRepository.findAll().stream().map(autor -> new AutoresResponse(
                autor.getId(),
                autor.getNomeCanal(),
                autor.getLinkCanal()
        )).toList();
    }

    public String mudarAutor(Long id, AutoresRequest autorAlterado) {
        Autores autor = autoresRepository.findById(id).orElseThrow();

        autor.setNomeCanal(autorAlterado.getNomeCanal());
        autor.setLinkCanal(autorAlterado.getLinkCanal());

        autoresRepository.save(autor);
        return "Autor alterado";
    }

    public String deletarId(Long id) {
        Optional<Autores> autor = autoresRepository.findById(id);

        if (autor.isEmpty()) {
            return "Esse autor não foi cadastrado";
        } else {
            autoresRepository.deleteById(id);
            return "Autor excluído";
        }
    }
}
