package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.CourseModuleRequest;
import com.huariservice.huariia.dto.CourseModuleResponse;
import com.huariservice.huariia.entities.Course;
import com.huariservice.huariia.entities.CourseModule;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CourseModuleRepository;
import com.huariservice.huariia.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for managing {@link CourseModule} entities (ordered sections of a course).
 *
 * <p>The main rule enforced here is that two modules of the same course cannot share the same
 * position; this mirrors the database unique constraint to return a friendly 409.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CourseModuleService {

    private final CourseModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    /**
     * Creates a new module.
     * <ol>
     *   <li>Rejects the request if the position is already taken inside the course.</li>
     *   <li>Fills a new entity (resolving the course) and persists it.</li>
     * </ol>
     *
     * @param request validated payload
     * @return the persisted module
     * @throws ConflitoException             if the position is already used in that course
     * @throws RecursoNaoEncontradoException if the course does not exist
     */
    public CourseModuleResponse createModule(CourseModuleRequest request) {
        if (moduleRepository.existsByCourseIdAndPosition(request.courseId(), request.position())) {
            throw new ConflitoException("A module with this position already exists in the course");
        }

        CourseModule module = new CourseModule();
        applyRequest(module, request);
        return CourseModuleResponse.from(moduleRepository.save(module));
    }

    /**
     * Lists every module of every course.
     *
     * @return all modules (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<CourseModuleResponse> listModules() {
        return moduleRepository.findAll().stream().map(CourseModuleResponse::from).toList();
    }

    /**
     * Lists the modules of one course, ordered by position.
     *
     * @param courseId course identifier
     * @return the course modules sorted by ascending position
     */
    @Transactional(readOnly = true)
    public List<CourseModuleResponse> listModulesByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByPositionAsc(courseId).stream()
                .map(CourseModuleResponse::from).toList();
    }

    /**
     * Retrieves a single module.
     *
     * @param id module identifier
     * @return the module as a response DTO
     * @throws RecursoNaoEncontradoException if no module has this id
     */
    @Transactional(readOnly = true)
    public CourseModuleResponse findById(Long id) {
        return CourseModuleResponse.from(findModuleOrThrow(id));
    }

    /**
     * Updates an existing module.
     * <ol>
     *   <li>Loads the module (404 if missing).</li>
     *   <li>Detects whether the (course, position) pair changed; only then checks the target
     *       position for conflicts, so re-saving the same position is allowed.</li>
     *   <li>Re-applies all fields and saves.</li>
     * </ol>
     *
     * @param id      module identifier
     * @param request validated payload with the new values
     * @return the updated module
     * @throws RecursoNaoEncontradoException if the module or the course does not exist
     * @throws ConflitoException             if the target position is already taken
     */
    public CourseModuleResponse updateModule(Long id, CourseModuleRequest request) {
        CourseModule module = findModuleOrThrow(id);

        boolean positionChanged = !module.getCourse().getId().equals(request.courseId())
                || !module.getPosition().equals(request.position());
        if (positionChanged
                && moduleRepository.existsByCourseIdAndPosition(request.courseId(), request.position())) {
            throw new ConflitoException("A module with this position already exists in the course");
        }

        applyRequest(module, request);
        return CourseModuleResponse.from(moduleRepository.save(module));
    }

    /**
     * Deletes a module by id.
     *
     * @param id module identifier
     * @throws RecursoNaoEncontradoException if no module has this id
     */
    public void deleteById(Long id) {
        moduleRepository.delete(findModuleOrThrow(id));
    }

    /**
     * Copies the request values into the entity, resolving the owning course.
     *
     * @param module  the entity to fill (new or managed)
     * @param request the source payload
     * @throws RecursoNaoEncontradoException if the course does not exist
     */
    private void applyRequest(CourseModule module, CourseModuleRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Course not found"));

        module.setTitle(request.title());
        module.setDescription(request.description());
        module.setPosition(request.position());
        module.setCourse(course);
    }

    /**
     * Loads a module or fails with a 404-mapped exception.
     *
     * @param id module identifier
     * @return the managed entity
     */
    private CourseModule findModuleOrThrow(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Module not found"));
    }
}