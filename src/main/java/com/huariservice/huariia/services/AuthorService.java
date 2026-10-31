package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.AuthorRequest;
import com.huariservice.huariia.DTOs.AuthorResponse;
import com.huariservice.huariia.entities.Author;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business rules for course authors (content channels).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthorService {

    private static final String NAME_TAKEN = "Já existe um autor com esse nome de canal";
    private static final String URL_TAKEN = "Já existe um autor com esse link de canal";

    private final AuthorRepository authorRepository;

    /**
     * Creates an author.
     *
     * @param request channel name and URL
     * @return the created author
     * @throws ConflictException if the channel name or URL is already taken
     */
    public AuthorResponse create(AuthorRequest request) {
        if (authorRepository.existsByChannelName(request.channelName())) {
            throw new ConflictException(NAME_TAKEN);
        }
        if (authorRepository.existsByChannelUrl(request.channelUrl())) {
            throw new ConflictException(URL_TAKEN);
        }

        Author author = new Author();
        author.setChannelName(request.channelName());
        author.setChannelUrl(request.channelUrl());

        return AuthorResponse.from(authorRepository.save(author));
    }

    /**
     * Lists all authors.
     *
     * @return every author
     */
    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream().map(AuthorResponse::from).toList();
    }

    /**
     * Finds an author by id.
     *
     * @param id the author id
     * @return the author
     * @throws ResourceNotFoundException if the author does not exist
     */
    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        return AuthorResponse.from(getAuthor(id));
    }

    /**
     * Updates an author.
     *
     * @param id      the author id
     * @param request new channel name and URL
     * @return the updated author
     * @throws ResourceNotFoundException if the author does not exist
     * @throws ConflictException         if the new name or URL belongs to another
     *                                   author
     */
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = getAuthor(id);

        if (!author.getChannelName().equals(request.channelName())
                && authorRepository.existsByChannelName(request.channelName())) {
            throw new ConflictException(NAME_TAKEN);
        }
        if (!author.getChannelUrl().equals(request.channelUrl())
                && authorRepository.existsByChannelUrl(request.channelUrl())) {
            throw new ConflictException(URL_TAKEN);
        }

        author.setChannelName(request.channelName());
        author.setChannelUrl(request.channelUrl());

        return AuthorResponse.from(authorRepository.save(author));
    }

    /**
     * Deletes an author.
     *
     * @param id the author id
     * @throws ResourceNotFoundException if the author does not exist
     */
    public void delete(Long id) {
        authorRepository.delete(getAuthor(id));
    }

    private Author getAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esse autor não foi cadastrado"));
    }
}
