package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CategoryRequest;
import com.huariservice.huariia.DTOs.CategoryResponse;
import com.huariservice.huariia.entities.Category;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business rules for course categories.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private static final String NAME_TAKEN = "Já existe uma categoria com esse nome";

    private final CategoryRepository categoryRepository;

    /**
     * Creates a category.
     *
     * @param request name and description
     * @return the created category
     * @throws ConflictException if the name is already taken
     */
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ConflictException(NAME_TAKEN);
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        return CategoryResponse.from(categoryRepository.save(category));
    }

    /**
     * Lists all categories.
     *
     * @return every category
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    /**
     * Finds a category by id.
     *
     * @param id the category id
     * @return the category
     * @throws ResourceNotFoundException if the category does not exist
     */
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(getCategory(id));
    }

    /**
     * Updates a category.
     *
     * @param id      the category id
     * @param request new name and description
     * @return the updated category
     * @throws ResourceNotFoundException if the category does not exist
     * @throws ConflictException         if the new name belongs to another category
     */
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getCategory(id);

        if (!category.getName().equals(request.name())
                && categoryRepository.existsByName(request.name())) {
            throw new ConflictException(NAME_TAKEN);
        }

        category.setName(request.name());
        category.setDescription(request.description());

        return CategoryResponse.from(categoryRepository.save(category));
    }

    /**
     * Deletes a category.
     *
     * @param id the category id
     * @throws ResourceNotFoundException if the category does not exist
     */
    public void delete(Long id) {
        categoryRepository.delete(getCategory(id));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Essa categoria não foi cadastrada"));
    }
}
