package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.LessonStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * JPA entity that tracks how far a given {@link User} has progressed in a given {@link Lesson}.
 */
@Entity
@Table(name = "progresso_aula", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "aula_id"})
}, indexes = {
        @Index(name = "idx_progresso_usuario", columnList = "usuario_id"),
        @Index(name = "idx_progresso_aula", columnList = "aula_id")
})
@SQLDelete(sql = "UPDATE progresso_aula SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgress extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aula", nullable = false)
    @ToString.Include
    private LessonStatus status = LessonStatus.NOT_STARTED;

    @Column(name = "data_conclusao")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false)
    @ToString.Exclude
    private Lesson lesson;

    public void complete() {
        this.status = LessonStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonProgress other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
