package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.CourseModuleRequest;
import com.huariservice.huariia.DTOs.CourseModuleResponse;
import com.huariservice.huariia.entities.Course;
import com.huariservice.huariia.entities.CourseModule;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.CourseModuleRepository;
import com.huariservice.huariia.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business rules for course modules.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CourseModuleService {

    private static final String POSITION_TAKEN = "Já existe um módulo com essa ordem no curso";

    private final CourseModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    /**
     * Creates a module inside a course.
     *
     * @param request module data, including the course id and position
     * @return the created module
     * @throws ConflictException         if the position is already used in the course
     * @throws ResourceNotFoundException if the course does not exist
     */
    public CourseModuleResponse create(CourseModuleRequest request) {
        if (moduleRepository.existsByCourseIdAndPosition(request.courseId(), request.position())) {
            throw new ConflictException(POSITION_TAKEN);
        }

        CourseModule module = new CourseModule();
        apply(module, request);

        return CourseModuleResponse.from(moduleRepository.save(module));
    }

    /**
     * Lists all modules.
     *
     * @return every module
     */
    @Transactional(readOnly = true)
    public List<CourseModuleResponse> findAll() {
        return moduleRepository.findAll().stream().map(CourseModuleResponse::from).toList();
    }

    /**
     * Lists the modules of a course in position order.
     *
     * @param courseId the course id
     * @return the course's modules
     */
    @Transactional(readOnly = true)
    public List<CourseModuleResponse> findByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByPositionAsc(courseId).stream()
                .map(CourseModuleResponse::from).toList();
    }

    /**
     * Finds a module by id.
     *
     * @param id the module id
     * @return the module
     * @throws ResourceNotFoundException if the module does not exist
     */
    @Transactional(readOnly = true)
    public CourseModuleResponse findById(Long id) {
        return CourseModuleResponse.from(getModule(id));
    }

    /**
     * Updates a module.
     *
     * @param id      the module id
     * @param request new module data
     * @return the updated module
     * @throws ResourceNotFoundException if the module or course does not exist
     * @throws ConflictException         if the new position is already used in the target course
     */
    public CourseModuleResponse update(Long id, CourseModuleRequest request) {
        CourseModule module = getModule(id);

        boolean positionChanged = !module.getCourse().getId().equals(request.courseId())
                || !module.getPosition().equals(request.position());
        if (positionChanged
                && moduleRepository.existsByCourseIdAndPosition(request.courseId(), request.position())) {
            throw new ConflictException(POSITION_TAKEN);
        }

        apply(module, request);

        return CourseModuleResponse.from(moduleRepository.save(module));
    }

    /**
     * Deletes a module.
     *
     * @param id the module id
     * @throws ResourceNotFoundException if the module does not exist
     */
    public void delete(Long id) {
        moduleRepository.delete(getModule(id));
    }

    private void apply(CourseModule module, CourseModuleRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Esse curso não foi publicado"));

        module.setTitle(request.title());
        module.setDescription(request.description());
        module.setPosition(request.position());
        module.setCourse(course);
    }

    private CourseModule getModule(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esse módulo não foi cadastrado"));
    }
}
