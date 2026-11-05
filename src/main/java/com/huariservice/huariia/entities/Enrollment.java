package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * JPA entity representing the enrollment of a {@link User} in a {@link Course}.
 */
@Entity
@Table(name = "matriculas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "curso_id"})
}, indexes = {
        @Index(name = "idx_matricula_usuario", columnList = "usuario_id"),
        @Index(name = "idx_matricula_curso", columnList = "curso_id")
})
@SQLDelete(sql = "UPDATE matriculas SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "data_matricula", nullable = false, updatable = false)
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_matricula", nullable = false)
    @ToString.Include
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @ToString.Exclude
    private Course course;

    @PrePersist
    void onCreate() {
        if (enrollmentDate == null) {
            this.enrollmentDate = LocalDate.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
