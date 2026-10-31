package com.huariservice.huariia.dto;

import com.huariservice.huariia.entities.Category;

/**
 * Outgoing representation of a {@link Category} returned by the API.
 *
 * @param id          database identifier
 * @param name        category name
 * @param description optional description
 */
public record CategoryResponse(
        Long id,
        String name,
        String description
) {

    /**
     * Converts a managed {@link Category} entity into its API representation.
     *
     * @param category the entity to convert (must not be {@code null})
     * @return a new immutable {@code CategoryResponse}
     */
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
