package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Checks whether a category with the given name already exists.
     *
     * @param name the category name to check
     * @return {@code true} if the name is already taken
     */
    boolean existsByName(String name);
}
