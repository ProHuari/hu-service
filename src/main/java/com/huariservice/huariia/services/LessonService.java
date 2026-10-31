package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.LessonRequest;
import com.huariservice.huariia.dto.LessonResponse;
import com.huariservice.huariia.entities.CourseModule;
import com.huariservice.huariia.entities.Lesson;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CourseModuleRepository;
import com.huariservice.huariia.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for managing {@link Lesson} entities (individual video lessons).
 *
 * <p>Enforces two uniqueness rules: the video URL is globally unique, and a position can be
 * used only once per module.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseModuleRepository moduleRepository;

    /**
     * Creates a new lesson.
     * <ol>
     *   <li>Rejects a duplicated video URL.</li>
     *   <li>Rejects a position already used inside the target module.</li>
     *   <li>Fills a new entity (resolving the module) and persists it.</li>
     * </ol>
     *
     * @param request validated payload
     * @return the persisted lesson
     * @throws ConflitoException             if the URL or the position is already used
     * @throws RecursoNaoEncontradoException if the module does not exist
     */
    public LessonResponse createLesson(LessonRequest request) {
        if (lessonRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflitoException("A lesson with this video URL already exists");
        }
        if (lessonRepository.existsByModuleIdAndPosition(request.moduleId(), request.position())) {
            throw new ConflitoException("A lesson with this position already exists in the module");
        }

        Lesson lesson = new Lesson();
        applyRequest(lesson, request);
        return LessonResponse.from(lessonRepository.save(lesson));
    }

    /**
     * Lists every lesson.
     *
     * @return all lessons (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> listLessons() {
        return lessonRepository.findAll().stream().map(LessonResponse::from).toList();
    }

    /**
     * Lists the lessons of one module, ordered by position.
     *
     * @param moduleId module identifier
     * @return the module lessons sorted by ascending position
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> listLessonsByModule(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByPositionAsc(moduleId).stream()
                .map(LessonResponse::from).toList();
    }

    /**
     * Retrieves a single lesson.
     *
     * @param id lesson identifier
     * @return the lesson as a response DTO
     * @throws RecursoNaoEncontradoException if no lesson has this id
     */
    @Transactional(readOnly = true)
    public LessonResponse findById(Long id) {
        return LessonResponse.from(findLessonOrThrow(id));
    }

    /**
     * Updates an existing lesson.
     * <ol>
     *   <li>Loads the lesson (404 if missing).</li>
     *   <li>Checks the video URL for duplicates only if it changed.</li>
     *   <li>Checks the target position only if the (module, position) pair changed.</li>
     *   <li>Re-applies all fields and saves.</li>
     * </ol>
     *
     * @param id      lesson identifier
     * @param request validated payload with the new values
     * @return the updated lesson
     * @throws RecursoNaoEncontradoException if the lesson or the module does not exist
     * @throws ConflitoException             if the URL or the target position is already used
     */
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = findLessonOrThrow(id);

        if (!lesson.getVideoUrl().equals(request.videoUrl())
                && lessonRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflitoException("A lesson with this video URL already exists");
        }

        boolean positionChanged = !lesson.getModule().getId().equals(request.moduleId())
                || !lesson.getPosition().equals(request.position());
        if (positionChanged
                && lessonRepository.existsByModuleIdAndPosition(request.moduleId(), request.position())) {
            throw new ConflitoException("A lesson with this position already exists in the module");
        }

        applyRequest(lesson, request);
        return LessonResponse.from(lessonRepository.save(lesson));
    }

    /**
     * Deletes a lesson by id.
     *
     * @param id lesson identifier
     * @throws RecursoNaoEncontradoException if no lesson has this id
     */
    public void deleteById(Long id) {
        lessonRepository.delete(findLessonOrThrow(id));
    }

    /**
     * Copies the request values into the entity, resolving the owning module.
     *
     * @param lesson  the entity to fill (new or managed)
     * @param request the source payload
     * @throws RecursoNaoEncontradoException if the module does not exist
     */
    private void applyRequest(Lesson lesson, LessonRequest request) {
        CourseModule module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Module not found"));

        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setPosition(request.position());
        lesson.setDurationInMinutes(request.durationInMinutes());
        lesson.setModule(module);
    }

    /**
     * Loads a lesson or fails with a 404-mapped exception.
     *
     * @param id lesson identifier
     * @return the managed entity
     */
    private Lesson findLessonOrThrow(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lesson not found"));
    }
}