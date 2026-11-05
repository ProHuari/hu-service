package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.CategoryRequest;
import com.huariservice.huariia.dto.CategoryResponse;
import com.huariservice.huariia.entities.Category;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for managing {@link Category} entities (course subjects).
 *
 * <p>Transactional by default; read-only methods are marked {@code readOnly = true}.
 * The category name must be unique and this rule is checked before saving.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Creates a new category.
     * <ol>
     *   <li>Rejects the request if the name is already used.</li>
     *   <li>Builds the entity and persists it.</li>
     * </ol>
     *
     * @param request validated payload with name and optional description
     * @return the persisted category
     * @throws ConflitoException if a category with this name already exists
     */
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ConflictException("A category with this name already exists");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    /**
     * Lists every category.
     *
     * @return all categories (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    /**
     * Retrieves a single category.
     *
     * @param id category identifier
     * @return the category as a response DTO
     * @throws ResourceNotFoundException if no category has this id
     */
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(findCategoryOrThrow(id));
    }

    /**
     * Updates an existing category.
     * <ol>
     *   <li>Loads the category (404 if missing).</li>
     *   <li>Checks for a duplicate name only when the name actually changed.</li>
     *   <li>Copies the new values and saves.</li>
     * </ol>
     *
     * @param id      category identifier
     * @param request validated payload with the new values
     * @return the updated category
     * @throws ResourceNotFoundException if no category has this id
     * @throws ConflictException         if the new name belongs to another category
     */
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);

        if (!category.getName().equals(request.name())
                && categoryRepository.existsByName(request.name())) {
            throw new ConflictException("A category with this name already exists");
        }

        category.setName(request.name());
        category.setDescription(request.description());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    /**
     * Deletes a category by id.
     *
     * @param id category identifier
     * @throws ResourceNotFoundException if no category has this id
     */
    public void deleteById(Long id) {
        categoryRepository.delete(findCategoryOrThrow(id));
    }

    /**
     * Loads a category or fails with a 404-mapped exception.
     *
     * @param id category identifier
     * @return the managed entity
     */
    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
