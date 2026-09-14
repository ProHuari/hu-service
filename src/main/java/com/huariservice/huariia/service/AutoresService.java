package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOS.AutoresRequest;
import com.huariservice.huariia.entities.Autores;
import com.huariservice.huariia.repositories.AutoresRepository;
import org.springframework.stereotype.Service;

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
}
