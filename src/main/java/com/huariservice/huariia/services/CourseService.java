package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.CourseModuleResponse;
import com.huariservice.huariia.dto.CourseRequest;
import com.huariservice.huariia.dto.CourseResponse;
import com.huariservice.huariia.dto.LessonResponse;
import com.huariservice.huariia.entities.Author;
import com.huariservice.huariia.entities.Category;
import com.huariservice.huariia.entities.Course;
import com.huariservice.huariia.entities.CourseModule;
import com.huariservice.huariia.entities.Lesson;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.AuthorRepository;
import com.huariservice.huariia.repositories.CategoryRepository;
import com.huariservice.huariia.repositories.CourseModuleRepository;
import com.huariservice.huariia.repositories.CourseRepository;
import com.huariservice.huariia.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;

    /**
     * Creates a new course.
     *
     * @param request validated payload
     * @return the persisted course with nested category and author
     * @throws ConflictException         if the title or the video URL is already used
     * @throws ResourceNotFoundException if the category or the author does not exist
     */
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByTitle(request.title())) {
            throw new ConflictException("A course with this title already exists");
        }
        if (request.videoUrl() != null && courseRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflictException("A course with this video URL already exists");
        }

        Course course = new Course();
        applyRequest(course, request);
        Course saved = courseRepository.save(course);
        return CourseResponse.fromWithModules(saved, List.of());
    }

    /**
     * Lists every course with its nested modules and lessons.
     *
     * @return all courses (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> listCourses() {
        return courseRepository.findAll().stream().map(course -> {
            List<CourseModuleResponse> modules = loadModulesWithLessons(course.getId());
            return CourseResponse.fromWithModules(course, modules);
        }).toList();
    }

    /**
     * Lists courses with pagination and nested modules/lessons.
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> listCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(course -> {
            List<CourseModuleResponse> modules = loadModulesWithLessons(course.getId());
            return CourseResponse.fromWithModules(course, modules);
        });
    }

    /**
     * Retrieves a single course with its full module and lesson tree.
     *
     * @param id course identifier
     * @return the course as a response DTO
     * @throws ResourceNotFoundException if no course has this id
     */
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = findCourseOrThrow(id);
        List<CourseModuleResponse> modules = loadModulesWithLessons(id);
        return CourseResponse.fromWithModules(course, modules);
    }

    private List<CourseModuleResponse> loadModulesWithLessons(Long courseId) {
        List<CourseModule> modules = courseModuleRepository.findByCourseIdOrderByPositionAsc(courseId);
        return modules.stream().map(mod -> {
            List<Lesson> lessons = lessonRepository.findByModuleIdOrderByPositionAsc(mod.getId());
            List<LessonResponse> lessonResponses = lessons.stream()
                    .map(LessonResponse::from)
                    .toList();
            return CourseModuleResponse.fromWithLessons(mod, lessonResponses);
        }).toList();
    }

    /**
     * Updates an existing course.
     *
     * @param id      course identifier
     * @param request validated payload with the new values
     * @return the updated course
     * @throws ResourceNotFoundException if the course, category or author does not exist
     * @throws ConflictException         if the new title or video URL belongs to another course
     */
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = findCourseOrThrow(id);

        if (!Objects.equals(course.getTitle(), request.title())
                && courseRepository.existsByTitle(request.title())) {
            throw new ConflictException("A course with this title already exists");
        }
        if (request.videoUrl() != null
                && !Objects.equals(course.getVideoUrl(), request.videoUrl())
                && courseRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflictException("A course with this video URL already exists");
        }

        applyRequest(course, request);
        Course saved = courseRepository.save(course);
        List<CourseModuleResponse> modules = loadModulesWithLessons(saved.getId());
        return CourseResponse.fromWithModules(saved, modules);
    }

    /**
     * Deletes a course by id.
     *
     * @param id course identifier
     * @throws ResourceNotFoundException if no course has this id
     */
    public void deleteById(Long id) {
        courseRepository.delete(findCourseOrThrow(id));
    }

    /**
     * Copies the request values into the entity, resolving the referenced category and author.
     *
     * @param course  the entity to fill (new or managed)
     * @param request the source payload
     * @throws ResourceNotFoundException if the category or the author does not exist
     */
    private void applyRequest(Course course, CourseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }
}