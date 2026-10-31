package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Author} entities.
 *
 * <p>Provides inherited CRUD operations plus uniqueness checks used by the service layer
 * to return friendly 409 (conflict) errors.</p>
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Checks whether an author with the given channel name already exists.
     *
     * @param channelName the channel name to check
     * @return {@code true} if the name is already taken
     */
    boolean existsByChannelName(String channelName);

    /**
     * Checks whether an author with the given channel URL already exists.
     *
     * @param channelUrl the channel URL to check
     * @return {@code true} if the URL is already registered
     */
    boolean existsByChannelUrl(String channelUrl);
}
