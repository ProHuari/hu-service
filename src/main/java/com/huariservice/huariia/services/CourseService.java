package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CourseRequest;
import com.huariservice.huariia.DTOs.CourseResponse;
import com.huariservice.huariia.entities.Author;
import com.huariservice.huariia.entities.Category;
import com.huariservice.huariia.entities.Course;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.AuthorRepository;
import com.huariservice.huariia.repositories.CategoryRepository;
import com.huariservice.huariia.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business rules for courses.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {

    private static final String TITLE_TAKEN = "Já existe um curso com esse título";
    private static final String VIDEO_URL_TAKEN = "Já existe um curso com essa URL de vídeo";

    private final CourseRepository courseRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Publishes a course.
     *
     * @param request course data, including category and author ids
     * @return the created course
     * @throws ConflictException         if the title or video URL is already taken
     * @throws ResourceNotFoundException if the category or author does not exist
     */
    public CourseResponse create(CourseRequest request) {
        if (courseRepository.existsByTitle(request.title())) {
            throw new ConflictException(TITLE_TAKEN);
        }
        if (courseRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflictException(VIDEO_URL_TAKEN);
        }

        Course course = new Course();
        apply(course, request);

        return CourseResponse.from(courseRepository.save(course));
    }

    /**
     * Lists all courses.
     *
     * @return every course
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream().map(CourseResponse::from).toList();
    }

    /**
     * Finds a course by id.
     *
     * @param id the course id
     * @return the course
     * @throws ResourceNotFoundException if the course does not exist
     */
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        return CourseResponse.from(getCourse(id));
    }

    /**
     * Updates a course.
     *
     * @param id      the course id
     * @param request new course data
     * @return the updated course
     * @throws ResourceNotFoundException if the course, category or author does not exist
     * @throws ConflictException         if the new title or video URL belongs to another course
     */
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = getCourse(id);

        if (!course.getTitle().equals(request.title())
                && courseRepository.existsByTitle(request.title())) {
            throw new ConflictException(TITLE_TAKEN);
        }
        if (!course.getVideoUrl().equals(request.videoUrl())
                && courseRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflictException(VIDEO_URL_TAKEN);
        }

        apply(course, request);

        return CourseResponse.from(courseRepository.save(course));
    }

    /**
     * Deletes a course.
     *
     * @param id the course id
     * @throws ResourceNotFoundException if the course does not exist
     */
    public void delete(Long id) {
        courseRepository.delete(getCourse(id));
    }

    private void apply(Course course, CourseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Essa categoria não foi cadastrada"));
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Esse autor não foi cadastrado"));

        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setVideoUrl(request.videoUrl());
        course.setCategory(category);
        course.setAuthor(author);
    }

    private Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esse curso não foi publicado"));
    }
}
