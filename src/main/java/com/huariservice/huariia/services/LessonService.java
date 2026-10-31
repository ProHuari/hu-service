package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.LessonRequest;
import com.huariservice.huariia.DTOs.LessonResponse;
import com.huariservice.huariia.entities.CourseModule;
import com.huariservice.huariia.entities.Lesson;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.CourseModuleRepository;
import com.huariservice.huariia.repositories.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business rules for lessons.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LessonService {

    private static final String VIDEO_URL_TAKEN = "Já existe uma aula com essa URL de vídeo";
    private static final String POSITION_TAKEN = "Já existe uma aula com essa ordem no módulo";

    private final LessonRepository lessonRepository;
    private final CourseModuleRepository moduleRepository;

    /**
     * Publishes a lesson inside a module.
     *
     * @param request lesson data, including the module id and position
     * @return the created lesson
     * @throws ConflictException         if the video URL is taken or the position is used in the module
     * @throws ResourceNotFoundException if the module does not exist
     */
    public LessonResponse create(LessonRequest request) {
        if (lessonRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflictException(VIDEO_URL_TAKEN);
        }
        if (lessonRepository.existsByModuleIdAndPosition(request.moduleId(), request.position())) {
            throw new ConflictException(POSITION_TAKEN);
        }

        Lesson lesson = new Lesson();
        apply(lesson, request);

        return LessonResponse.from(lessonRepository.save(lesson));
    }

    /**
     * Lists all lessons.
     *
     * @return every lesson
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> findAll() {
        return lessonRepository.findAll().stream().map(LessonResponse::from).toList();
    }

    /**
     * Lists the lessons of a module in position order.
     *
     * @param moduleId the module id
     * @return the module's lessons
     */
    @Transactional(readOnly = true)
    public List<LessonResponse> findByModule(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByPositionAsc(moduleId).stream()
                .map(LessonResponse::from).toList();
    }

    /**
     * Finds a lesson by id.
     *
     * @param id the lesson id
     * @return the lesson
     * @throws ResourceNotFoundException if the lesson does not exist
     */
    @Transactional(readOnly = true)
    public LessonResponse findById(Long id) {
        return LessonResponse.from(getLesson(id));
    }

    /**
     * Updates a lesson.
     *
     * @param id      the lesson id
     * @param request new lesson data
     * @return the updated lesson
     * @throws ResourceNotFoundException if the lesson or module does not exist
     * @throws ConflictException         if the new video URL or position is already taken
     */
    public LessonResponse update(Long id, LessonRequest request) {
        Lesson lesson = getLesson(id);

        if (!lesson.getVideoUrl().equals(request.videoUrl())
                && lessonRepository.existsByVideoUrl(request.videoUrl())) {
            throw new ConflictException(VIDEO_URL_TAKEN);
        }

        boolean positionChanged = !lesson.getModule().getId().equals(request.moduleId())
                || !lesson.getPosition().equals(request.position());
        if (positionChanged
                && lessonRepository.existsByModuleIdAndPosition(request.moduleId(), request.position())) {
            throw new ConflictException(POSITION_TAKEN);
        }

        apply(lesson, request);

        return LessonResponse.from(lessonRepository.save(lesson));
    }

    /**
     * Deletes a lesson.
     *
     * @param id the lesson id
     * @throws ResourceNotFoundException if the lesson does not exist
     */
    public void delete(Long id) {
        lessonRepository.delete(getLesson(id));
    }

    private void apply(Lesson lesson, LessonRequest request) {
        CourseModule module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Esse módulo não foi cadastrado"));

        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setPosition(request.position());
        lesson.setDurationInMinutes(request.durationInMinutes());
        lesson.setModule(module);
    }

    private Lesson getLesson(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Essa aula não foi publicada"));
    }
}
