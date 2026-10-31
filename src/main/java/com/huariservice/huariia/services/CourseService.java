package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.CourseRequest;
import com.huariservice.huariia.dto.CourseResponse;
import com.huariservice.huariia.entities.Author;
import com.huariservice.huariia.entities.Category;
import com.huariservice.huariia.entities.Course;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AuthorRepository;
import com.huariservice.huariia.repositories.CategoryRepository;
import com.huariservice.huariia.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for managing {@link Course} entities.
 *
 * <p>A course references a {@link Category} and an {@link Author} by id; this service resolves
 * both and validates title/video URL uniqueness. All methods run inside a transaction because
 * {@link CourseResponse#from(Course)} touches LAZY associations.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new course.
     * <ol>
     *   <li>Rejects duplicated titles and duplicated video URLs.</li>
     *   <li>Creates an empty entity and fills it via {@link #applyRequest(Course, CourseRequest)},
     *       which also resolves the category and the author.</li>
     *   <li>Persists the course.</li>
     * </ol>
     *
     * @param request validated payload
     * @return the persisted course with nested category and author
     * @throws ConflitoException             if the title or the video URL is already used
     * @throws RecursoNaoEncontradoException if the category or the author does not exist
     */
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByTitle(request.title())) {
            throw new ConflitoException("A course with this title already exists");
        }
        if (courseRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflitoException("A course with this video URL already exists");
        }

        Course course = new Course();
        applyRequest(course, request);
        return CourseResponse.from(courseRepository.save(course));
    }

    /**
     * Lists every course.
     *
     * @return all courses (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> listCourses() {
        return courseRepository.findAll().stream().map(CourseResponse::from).toList();
    }

    /**
     * Retrieves a single course.
     *
     * @param id course identifier
     * @return the course as a response DTO
     * @throws RecursoNaoEncontradoException if no course has this id
     */
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        return CourseResponse.from(findCourseOrThrow(id));
    }

    /**
     * Updates an existing course.
     * <ol>
     *   <li>Loads the course (404 if missing).</li>
     *   <li>Checks title and video URL duplicates only for values that actually changed.</li>
     *   <li>Re-applies every field, including category and author.</li>
     *   <li>Saves the entity.</li>
     * </ol>
     *
     * @param id      course identifier
     * @param request validated payload with the new values
     * @return the updated course
     * @throws RecursoNaoEncontradoException if the course, category or author does not exist
     * @throws ConflitoException             if the new title or video URL belongs to another course
     */
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = findCourseOrThrow(id);

        if (!course.getTitle().equals(request.title())
                && courseRepository.existsByTitle(request.title())) {
            throw new ConflitoException("A course with this title already exists");
        }
        if (!course.getVideoUrl().equals(request.videoUrl())
                && courseRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflitoException("A course with this video URL already exists");
        }

        applyRequest(course, request);
        return CourseResponse.from(courseRepository.save(course));
    }

    /**
     * Deletes a course by id.
     *
     * @param id course identifier
     * @throws RecursoNaoEncontradoException if no course has this id
     */
    public void deleteById(Long id) {
        courseRepository.delete(findCourseOrThrow(id));
    }

    /**
     * Copies the request values into the entity, resolving the referenced category and author.
     *
     * @param course  the entity to fill (new or managed)
     * @param request the source payload
     * @throws RecursoNaoEncontradoException if the category or the author does not exist
     */
    private void applyRequest(Course course, CourseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Category not found"));
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Author not found"));

        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setVideoUrl(request.videoUrl());
        course.setCategory(category);
        course.setAuthor(author);
    }

    /**
     * Loads a course or fails with a 404-mapped exception.
     *
     * @param id course identifier
     * @return the managed entity
     */
    private Course findCourseOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Course not found"));
    }
}