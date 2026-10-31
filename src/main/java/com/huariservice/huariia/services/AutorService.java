package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.AutorRequest;
import com.huariservice.huariia.dto.AutorResponse;
import com.huariservice.huariia.entities.Autor;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;


    public AutorResponse pubAutor(AutorRequest request) {
        if (autorRepository.existsByNomeCanal(request.nomeCanal())) {
            throw new ConflitoException("Já existe um autor com esse nome de canal");
        }
        if (autorRepository.existsByLinkCanal(request.linkCanal())) {
            throw new ConflitoException("Já existe um autor com esse link de canal");
        }

        Autor autor = new Autor();
        autor.setNomeCanal(request.nomeCanal());
        autor.setLinkCanal(request.linkCanal());

        return AutorResponse.from(autorRepository.save(autor));
    }

    @Transactional(readOnly = true)
    public List<AutorResponse> mostrarAutores() {
        return autorRepository.findAll().stream().map(AutorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AutorResponse buscarPorId(Long id) {
        return AutorResponse.from(buscarAutor(id));
    }

    public AutorResponse mudarAutor(Long id, AutorRequest request) {
        Autor autor = buscarAutor(id);

        if (!autor.getNomeCanal().equals(request.nomeCanal())
                && autorRepository.existsByNomeCanal(request.nomeCanal())) {
            throw new ConflitoException("Já existe um autor com esse nome de canal");
        }
        if (!autor.getLinkCanal().equals(request.linkCanal())
                && autorRepository.existsByLinkCanal(request.linkCanal())) {
            throw new ConflitoException("Já existe um autor com esse link de canal");
        }

        autor.setNomeCanal(request.nomeCanal());
        autor.setLinkCanal(request.linkCanal());

        return AutorResponse.from(autorRepository.save(autor));
    }

    public void deletarId(Long id) {
        autorRepository.delete(buscarAutor(id));
    }

    private Autor buscarAutor(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse autor não foi cadastrado"));
    }
}