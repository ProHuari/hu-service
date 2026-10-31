package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.EnrollmentRequest;
import com.huariservice.huariia.dto.EnrollmentResponse;
import com.huariservice.huariia.entities.Course;
import com.huariservice.huariia.entities.Enrollment;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.EnrollmentStatus;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.CourseRepository;
import com.huariservice.huariia.repositories.EnrollmentRepository;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for course enrollments.
 *
 * <p>Students only see their own enrollments; administrators see all. For students, accessing
 * someone else's record returns 404 instead of 403 so the existence of the id is not revealed.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Enrolls a user in a course. The user id comes from the authenticated token and never
     * from the request body, so a client cannot enroll somebody else.
     * <ol>
     *   <li>Loads the user and the course (404 if either is missing).</li>
     *   <li>Rejects the request if the user is already enrolled in that course.</li>
     *   <li>Creates the enrollment; date and status default to today and ACTIVE.</li>
     * </ol>
     *
     * @param userId  id of the authenticated user
     * @param request payload with the course id
     * @return the created enrollment
     * @throws RecursoNaoEncontradoException if the user or the course does not exist
     * @throws ConflitoException             if the user is already enrolled
     */
    public EnrollmentResponse enroll(Long userId, EnrollmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("User not found"));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Course not found"));

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new ConflitoException("The user is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    /**
     * Lists every enrollment (administrator view).
     *
     * @return all enrollments (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listEnrollments() {
        return enrollmentRepository.findAll().stream().map(EnrollmentResponse::from).toList();
    }

    /**
     * Lists the enrollments of one user ("my courses").
     *
     * @param userId user identifier
     * @return the user's enrollments (empty list if none)
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listEnrollmentsByUser(Long userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(EnrollmentResponse::from).toList();
    }

    /**
     * Retrieves one enrollment, applying ownership rules.
     * <ol>
     *   <li>Loads the enrollment (404 if missing).</li>
     *   <li>If the caller is not an admin and does not own it, answers 404 as if it did not exist.</li>
     * </ol>
     *
     * @param id      enrollment identifier
     * @param userId  id of the authenticated user
     * @param admin   whether the caller has the admin role
     * @return the enrollment
     * @throws RecursoNaoEncontradoException if it does not exist or is not visible to the caller
     */
    @Transactional(readOnly = true)
    public EnrollmentResponse findById(Long id, Long userId, boolean admin) {
        Enrollment enrollment = findEnrollmentOrThrow(id);

        if (!admin && !enrollment.getUser().getId().equals(userId)) {
            throw new RecursoNaoEncontradoException("Enrollment not found");
        }
        return EnrollmentResponse.from(enrollment);
    }

    /**
     * Changes only the status of an enrollment (e.g. ACTIVE to CANCELLED). The user and the
     * course cannot change after enrolling.
     *
     * @param id        enrollment identifier
     * @param newStatus the status to apply
     * @return the updated enrollment
     * @throws RecursoNaoEncontradoException if no enrollment has this id
     */
    public EnrollmentResponse updateStatus(Long id, EnrollmentStatus newStatus) {
        Enrollment enrollment = findEnrollmentOrThrow(id);
        enrollment.setStatus(newStatus);
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    /**
     * Deletes an enrollment by id.
     *
     * @param id enrollment identifier
     * @throws RecursoNaoEncontradoException if no enrollment has this id
     */
    public void deleteById(Long id) {
        enrollmentRepository.delete(findEnrollmentOrThrow(id));
    }

    /**
     * Loads an enrollment or fails with a 404-mapped exception.
     *
     * @param id enrollment identifier
     * @return the managed entity
     */
    private Enrollment findEnrollmentOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Enrollment not found"));
    }
}