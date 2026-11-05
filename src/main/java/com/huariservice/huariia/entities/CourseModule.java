package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * JPA entity representing an ordered section of a {@link Course} that groups related lessons.
 */
@Entity
@Table(name = "modulos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"curso_id", "ordem"})
})
@SQLDelete(sql = "UPDATE modulos SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseModule extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "titulo", length = 150, nullable = false)
    @ToString.Include
    private String title;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String description;

    @Min(1)
    @Column(name = "ordem", nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @ToString.Exclude
    private Course course;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseModule other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
