package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Author;

/**
 * Outgoing representation of an {@link Author} returned by the API.
 *
 * @param id          database identifier
 * @param channelName public channel name
 * @param channelUrl  URL of the channel
 */
public record AuthorResponse(
        Long id,
        String channelName,
        String channelUrl
) {

    /**
     * Converts a managed {@link Author} entity into its API representation.
     *
     * @param author the entity to convert (must not be {@code null})
     * @return a new immutable {@code AuthorResponse}
     */
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.getId(), author.getChannelName(), author.getChannelUrl());
    }
}
