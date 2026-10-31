package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.AuthorRequest;
import com.huariservice.huariia.dto.AuthorResponse;
import com.huariservice.huariia.entities.Author;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for managing {@link Author} entities (content creators).
 *
 * <p>The class is transactional by default; read-only methods override this with
 * {@code readOnly = true} so the persistence provider can skip dirty checking.
 * Uniqueness rules (channel name and URL) are enforced here to return a friendly
 * 409 error instead of a raw database constraint violation.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    /**
     * Creates a new author.
     * <ol>
     *   <li>Rejects the request if the channel name is already taken.</li>
     *   <li>Rejects the request if the channel URL is already registered.</li>
     *   <li>Builds the entity from the request and persists it.</li>
     * </ol>
     *
     * @param request validated payload with channel name and URL
     * @return the persisted author as a response DTO
     * @throws ConflitoException if the name or the URL already exists
     */
    public AuthorResponse createAuthor(AuthorRequest request) {
        if (authorRepository.existsByChannelName(request.channelName())) {
            throw new ConflitoException("An author with this channel name already exists");
        }
        if (authorRepository.existsByChannelUrl(request.channelUrl())) {
            throw new ConflitoException("An author with this channel URL already exists");
        }

        Author author = new Author();
        author.setChannelName(request.channelName());
        author.setChannelUrl(request.channelUrl());
        return AuthorResponse.from(authorRepository.save(author));
    }

    /**
     * Lists every author.
     *
     * @return all authors (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<AuthorResponse> listAuthors() {
        return authorRepository.findAll().stream().map(AuthorResponse::from).toList();
    }

    /**
     * Retrieves a single author.
     *
     * @param id author identifier
     * @return the author as a response DTO
     * @throws RecursoNaoEncontradoException if no author has this id
     */
    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        return AuthorResponse.from(findAuthorOrThrow(id));
    }

    /**
     * Updates an existing author.
     * <ol>
     *   <li>Loads the author (404 if missing).</li>
     *   <li>Checks the channel name for duplicates only when it actually changed, so saving an
     *       unchanged value does not conflict with the author's own row.</li>
     *   <li>Does the same duplicate check for the channel URL.</li>
     *   <li>Copies the new values and saves.</li>
     * </ol>
     *
     * @param id      author identifier
     * @param request validated payload with the new values
     * @return the updated author
     * @throws RecursoNaoEncontradoException if no author has this id
     * @throws ConflitoException             if the new name or URL belongs to another author
     */
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = findAuthorOrThrow(id);

        if (!author.getChannelName().equals(request.channelName())
                && authorRepository.existsByChannelName(request.channelName())) {
            throw new ConflitoException("An author with this channel name already exists");
        }
        if (!author.getChannelUrl().equals(request.channelUrl())
                && authorRepository.existsByChannelUrl(request.channelUrl())) {
            throw new ConflitoException("An author with this channel URL already exists");
        }

        author.setChannelName(request.channelName());
        author.setChannelUrl(request.channelUrl());
        return AuthorResponse.from(authorRepository.save(author));
    }

    /**
     * Deletes an author by id.
     *
     * @param id author identifier
     * @throws RecursoNaoEncontradoException if no author has this id
     */
    public void deleteById(Long id) {
        authorRepository.delete(findAuthorOrThrow(id));
    }

    /**
     * Loads an author or fails with a 404-mapped exception.
     *
     * @param id author identifier
     * @return the managed entity
     */
    private Author findAuthorOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Author not found"));
    }
}