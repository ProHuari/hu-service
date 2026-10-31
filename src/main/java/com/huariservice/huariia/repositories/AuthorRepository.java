package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link Author} entities.
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Checks whether an author with the given channel name already exists.
     *
     * @param channelName the channel name
     * @return {@code true} if the channel name is already taken
     */
    boolean existsByChannelName(String channelName);

    /**
     * Checks whether an author with the given channel URL already exists.
     *
     * @param channelUrl the channel URL
     * @return {@code true} if the channel URL is already taken
     */
    boolean existsByChannelUrl(String channelUrl);
}
