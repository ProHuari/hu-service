package com.huariservice.huariia.entities.enums;

/**
 * Authorization role assigned to a {@link com.huariservice.huariia.entities.User}.
 *
 * <p>Used by the security layer to decide which endpoints a user may call.
 * Persisted as a {@code STRING} in the {@code usuarios.tipo_perfil} column.</p>
 */
public enum Role {

    /** Full access: manages courses, modules, lessons, authors, categories and users. */
    ADMIN,

    /** Regular learner: enrolls in courses, tracks progress and queries the AI assistant (default). */
    STUDENT
}
