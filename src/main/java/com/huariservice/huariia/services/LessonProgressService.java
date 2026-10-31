package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.LessonProgressRequest;
import com.huariservice.huariia.DTOs.LessonProgressResponse;
import com.huariservice.huariia.entities.Lesson;
import com.huariservice.huariia.entities.LessonProgress;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.LessonStatus;
import com.huariservice.huariia.exceptions.BusinessRuleException;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.EnrollmentRepository;
import com.huariservice.huariia.repositories.LessonProgressRepository;
import com.huariservice.huariia.repositories.LessonRepository;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Business rules for tracking a user's progress in lessons.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LessonProgressService {

    private static final String PROGRESS_NOT_FOUND = "Esse progresso de aula não foi registrado";

    private final LessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Registers the progress of a user in a lesson. The user id always comes from
     * the token.
     *
     * @param userId  the authenticated user's id
     * @param request lesson id and status
     * @return the created progress record
     * @throws ResourceNotFoundException if the user or lesson does not exist
     * @throws BusinessRuleException     if the user is not enrolled in the lesson's
     *                                   course
     * @throws ConflictException         if progress was already registered for this
     *                                   lesson
     */
    public LessonProgressResponse create(Long userId, LessonProgressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Esse usuário não foi cadastrado"));
        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Essa aula não foi publicada"));

        Long courseId = lesson.getModule().getCourse().getId();
        if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new BusinessRuleException("O usuário não está matriculado no curso dessa aula");
        }
        if (progressRepository.findByUserIdAndLessonId(user.getId(), lesson.getId()).isPresent()) {
            throw new ConflictException("Já existe progresso registrado para essa aula; atualize o existente");
        }

        LessonProgress progress = new LessonProgress();
        progress.setUser(user);
        progress.setLesson(lesson);
        applyStatus(progress, request.status());

        return LessonProgressResponse.from(progressRepository.save(progress));
    }

    /**
     * Lists all progress records (admin view).
     *
     * @return every progress record
     */
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> findAll() {
        return progressRepository.findAll().stream().map(LessonProgressResponse::from).toList();
    }

    /**
     * Lists the progress records of a user.
     *
     * @param userId the user id
     * @return the user's progress records
     */
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> findByUser(Long userId) {
        return progressRepository.findByUserId(userId).stream()
                .map(LessonProgressResponse::from).toList();
    }

    /**
     * Finds a progress record by id (students only access their own).
     *
     * @param id      the progress id
     * @param userId  the authenticated user's id
     * @param isAdmin whether the authenticated user is an admin
     * @return the progress record
     * @throws ResourceNotFoundException if it does not exist or belongs to another
     *                                   user
     */
    @Transactional(readOnly = true)
    public LessonProgressResponse findById(Long id, Long userId, boolean isAdmin) {
        return LessonProgressResponse.from(getProgress(id, userId, isAdmin));
    }

    /**
     * Updates the status of a progress record. The user and lesson of the record
     * never change.
     *
     * @param id      the progress id
     * @param request the new status (the lesson id is ignored)
     * @param userId  the authenticated user's id
     * @param isAdmin whether the authenticated user is an admin
     * @return the updated progress record
     * @throws ResourceNotFoundException if it does not exist or belongs to another
     *                                   user
     */
    public LessonProgressResponse update(Long id, LessonProgressRequest request,
            Long userId, boolean isAdmin) {
        LessonProgress progress = getProgress(id, userId, isAdmin);
        applyStatus(progress, request.status());

        return LessonProgressResponse.from(progressRepository.save(progress));
    }

    /**
     * Deletes a progress record (students only delete their own).
     *
     * @param id      the progress id
     * @param userId  the authenticated user's id
     * @param isAdmin whether the authenticated user is an admin
     * @throws ResourceNotFoundException if it does not exist or belongs to another
     *                                   user
     */
    public void delete(Long id, Long userId, boolean isAdmin) {
        progressRepository.delete(getProgress(id, userId, isAdmin));
    }

    /**
     * Sets the status; {@code completedAt} only exists while the lesson is
     * COMPLETED.
     */
    private void applyStatus(LessonProgress progress, LessonStatus status) {
        progress.setStatus(status);
        if (status == LessonStatus.COMPLETED) {
            if (progress.getCompletedAt() == null) {
                progress.setCompletedAt(LocalDateTime.now());
            }
        } else {
            progress.setCompletedAt(null);
        }
    }

    /**
     * Students only access their own records. Returns 404 (not 403) to avoid
     * revealing that the id exists.
     */
    private LessonProgress getProgress(Long id, Long userId, boolean isAdmin) {
        LessonProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROGRESS_NOT_FOUND));

        if (!isAdmin && !progress.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(PROGRESS_NOT_FOUND);
        }
        return progress;
    }
}
