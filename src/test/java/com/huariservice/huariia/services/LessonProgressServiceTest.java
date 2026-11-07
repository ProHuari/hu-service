package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.LessonProgressRequest;
import com.huariservice.huariia.dto.LessonProgressResponse;
import com.huariservice.huariia.entities.*;
import com.huariservice.huariia.entities.enums.LessonStatus;
import com.huariservice.huariia.exceptions.BusinessRuleException;
import com.huariservice.huariia.repositories.EnrollmentRepository;
import com.huariservice.huariia.repositories.LessonProgressRepository;
import com.huariservice.huariia.repositories.LessonRepository;
import com.huariservice.huariia.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonProgressServiceTest {

    @Mock
    private LessonProgressRepository progressRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private LessonProgressService lessonProgressService;

    @Test
    @DisplayName("Should create new progress record when none exists (Upsert insert)")
    void shouldCreateNewProgressRecord() {
        Long userId = 1L;
        Long lessonId = 10L;
        Long courseId = 100L;

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);

        CourseModule module = new CourseModule();
        module.setId(5L);
        module.setCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setModule(module);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);
        when(progressRepository.findByUserIdAndLessonId(userId, lessonId)).thenReturn(Optional.empty());

        when(progressRepository.save(any(LessonProgress.class))).thenAnswer(invocation -> {
            LessonProgress p = invocation.getArgument(0);
            p.setId(99L);
            return p;
        });

        LessonProgressRequest request = new LessonProgressRequest(LessonStatus.IN_PROGRESS, lessonId);
        LessonProgressResponse response = lessonProgressService.createProgress(userId, request);

        assertNotNull(response);
        assertEquals(99L, response.id());
        assertEquals(LessonStatus.IN_PROGRESS, response.status());
        assertNull(response.completedAt());
        verify(progressRepository).save(any(LessonProgress.class));
    }

    @Test
    @DisplayName("Should update existing progress record when it already exists (Upsert update)")
    void shouldUpdateExistingProgressRecord() {
        Long userId = 1L;
        Long lessonId = 10L;
        Long courseId = 100L;

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);

        CourseModule module = new CourseModule();
        module.setId(5L);
        module.setCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setModule(module);

        LessonProgress existing = new LessonProgress();
        existing.setId(77L);
        existing.setUser(user);
        existing.setLesson(lesson);
        existing.setStatus(LessonStatus.IN_PROGRESS);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);
        when(progressRepository.findByUserIdAndLessonId(userId, lessonId)).thenReturn(Optional.of(existing));

        when(progressRepository.save(any(LessonProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LessonProgressRequest request = new LessonProgressRequest(LessonStatus.COMPLETED, lessonId);
        LessonProgressResponse response = lessonProgressService.createProgress(userId, request);

        assertNotNull(response);
        assertEquals(77L, response.id());
        assertEquals(LessonStatus.COMPLETED, response.status());
        assertNotNull(response.completedAt());
        verify(progressRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when user is not enrolled in the course")
    void shouldThrowExceptionWhenUserNotEnrolled() {
        Long userId = 1L;
        Long lessonId = 10L;
        Long courseId = 100L;

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);

        CourseModule module = new CourseModule();
        module.setId(5L);
        module.setCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setModule(module);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);

        LessonProgressRequest request = new LessonProgressRequest(LessonStatus.COMPLETED, lessonId);
        assertThrows(BusinessRuleException.class, () -> lessonProgressService.createProgress(userId, request));
        verify(progressRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should filter progress by courseId and userId")
    void shouldFilterProgressByCourseIdAndUserId() {
        Long userId = 1L;
        Long courseId = 100L;
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);

        CourseModule module = new CourseModule();
        module.setId(5L);
        module.setCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId(10L);
        lesson.setModule(module);

        LessonProgress progress = new LessonProgress();
        progress.setId(1L);
        progress.setUser(user);
        progress.setLesson(lesson);
        progress.setStatus(LessonStatus.COMPLETED);
        progress.setCompletedAt(LocalDateTime.now());

        Page<LessonProgress> page = new PageImpl<>(List.of(progress));
        when(progressRepository.findByUserIdAndLessonModuleCourseId(userId, courseId, pageable))
                .thenReturn(page);

        Page<LessonProgressResponse> result = lessonProgressService.listProgressByUser(userId, courseId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).id());
        verify(progressRepository).findByUserIdAndLessonModuleCourseId(userId, courseId, pageable);
    }
}
