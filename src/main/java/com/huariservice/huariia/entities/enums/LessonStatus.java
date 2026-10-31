package com.huariservice.huariia.entities.enums;

/**
 * Represents the progress state of a single {@link com.huariservice.huariia.entities.Lesson}
 * for a specific user.
 *
 * <p>The value is persisted as a {@code STRING} in the {@code progresso_aula.status_aula} column,
 * so renaming a constant requires a data migration.</p>
 */
public enum LessonStatus {

    /** The user has not opened the lesson yet (default state). */
    NOT_STARTED,

    /** The user has started watching the lesson but has not finished it. */
    IN_PROGRESS,

    /** The user has finished the lesson; a completion timestamp is recorded. */
    COMPLETED
}
