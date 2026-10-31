package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.LessonProgressRequest;
import com.huariservice.huariia.dto.LessonProgressResponse;
import com.huariservice.huariia.entities.Lesson;
import com.huariservice.huariia.entities.LessonProgress;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.LessonStatus;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.exceptions.RegraNegocioException;
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
 * Business logic for tracking a user's progress on lessons.
 *
 * <p>A user can only track lessons of courses they are enrolled in, and there is at most one
 * progress record per (user, lesson). Students only access their own records; administrators
 * access all.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LessonProgressService {

    private final LessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * Creates the progress record of a user for a lesson. The user id comes from the token,
     * never from the request body.
     * <ol>
     *   <li>Loads the user and the lesson (404 if missing).</li>
     *   <li>Walks lesson, module, course to find the course id and verifies the user is
     *       enrolled in it.</li>
     *   <li>Rejects the request if a record already exists (the client must update it instead).</li>
     *   <li>Applies the requested status and persists the record.</li>
     * </ol>
     *
     * @param userId  id of the authenticated user
     * @param request payload with lesson id and status
     * @return the created progress record
     * @throws RecursoNaoEncontradoException if the user or the lesson does not exist
     * @throws RegraNegocioException         if the user is not enrolled in the lesson's course
     * @throws ConflitoException             if progress already exists for this lesson
     */
    public LessonProgressResponse createProgress(Long userId, LessonProgressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("User not found"));
        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lesson not found"));

        Long courseId = lesson.getModule().getCourse().getId();
        if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new RegraNegocioException("The user is not enrolled in the course of this lesson");
        }
        if (progressRepository.findByUserIdAndLessonId(user.getId(), lesson.getId()).isPresent()) {
            throw new ConflitoException("Progress already exists for this lesson; update the existing record");
        }

        LessonProgress progress = new LessonProgress();
        progress.setUser(user);
        progress.setLesson(lesson);
        applyStatus(progress, request.status());
        return LessonProgressResponse.from(progressRepository.save(progress));
    }

    /**
     * Lists every progress record (administrator view).
     *
     * @return all records (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> listProgress() {
        return progressRepository.findAll().stream().map(LessonProgressResponse::from).toList();
    }

    /**
     * Lists the progress records of one user.
     *
     * @param userId user identifier
     * @return the user's records (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<LessonProgressResponse> listProgressByUser(Long userId) {
        return progressRepository.findByUserId(userId).stream()
                .map(LessonProgressResponse::from).toList();
    }

    /**
     * Retrieves one progress record, applying ownership rules.
     *
     * @param id     record identifier
     * @param userId id of the authenticated user
     * @param admin  whether the caller has the admin role
     * @return the record
     * @throws RecursoNaoEncontradoException if it does not exist or is not visible to the caller
     */
    @Transactional(readOnly = true)
    public LessonProgressResponse findById(Long id, Long userId, boolean admin) {
        return LessonProgressResponse.from(findProgressOrThrow(id, userId, admin));
    }

    /**
     * Updates only the status of a record; its user and lesson can never be swapped.
     *
     * @param id      record identifier
     * @param request payload with the new status
     * @param userId  id of the authenticated user
     * @param admin   whether the caller has the admin role
     * @return the updated record
     * @throws RecursoNaoEncontradoException if it does not exist or is not visible to the caller
     */
    public LessonProgressResponse updateProgress(Long id, LessonProgressRequest request,
                                                 Long userId, boolean admin) {
        LessonProgress progress = findProgressOrThrow(id, userId, admin);
        applyStatus(progress, request.status());
        return LessonProgressResponse.from(progressRepository.save(progress));
    }

    /**
     * Deletes a progress record, applying ownership rules.
     *
     * @param id     record identifier
     * @param userId id of the authenticated user
     * @param admin  whether the caller has the admin role
     * @throws RecursoNaoEncontradoException if it does not exist or is not visible to the caller
     */
    public void deleteById(Long id, Long userId, boolean admin) {
        progressRepository.delete(findProgressOrThrow(id, userId, admin));
    }

    /**
     * Applies a status keeping the completion timestamp consistent:
     * <ul>
     *   <li>{@code COMPLETED}: sets {@code completedAt} to now, unless it is already set
     *       (the original completion time is preserved).</li>
     *   <li>Any other status: clears {@code completedAt}.</li>
     * </ul>
     *
     * @param progress the record to modify
     * @param status   the new status
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
     * Loads a record enforcing ownership. A non-admin asking for somebody else's record gets a
     * 404 (not 403) so the existence of the id is not revealed.
     *
     * @param id     record identifier
     * @param userId id of the authenticated user
     * @param admin  whether the caller has the admin role
     * @return the managed entity
     */
    private LessonProgress findProgressOrThrow(Long id, Long userId, boolean admin) {
        LessonProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lesson progress not found"));

        if (!admin && !progress.getUser().getId().equals(userId)) {
            throw new RecursoNaoEncontradoException("Lesson progress not found");
        }
        return progress;
    }
}