package com.huariservice.huariia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * Incoming payload used to create or update a {@link com.huariservice.huariia.entities.Course}.
 *
 * <p>The category and author are referenced by id; the service layer resolves them and
 * returns HTTP 404 if either does not exist.</p>
 *
 * @param title       unique course title (required, max 155 chars)
 * @param description full course description (required)
 * @param videoUrl    unique presentation video URL (required, valid URL, max 254 chars)
 * @param categoryId  id of an existing category (required)
 * @param authorId    id of an existing author (required)
 */
public record CourseRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 155, message = "Title must be at most 155 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Video URL is required")
        @Size(max = 254, message = "Video URL must be at most 254 characters")
        @URL(message = "Invalid video URL")
        String videoUrl,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "Author is required")
        Long authorId
) {}
