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
 * JPA entity representing a single video lesson that belongs to a {@link CourseModule}.
 */
@Entity
@Table(name = "aulas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"modulo_id", "ordem"})
})
@SQLDelete(sql = "UPDATE aulas SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lesson extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "titulo", length = 155, nullable = false)
    @ToString.Include
    private String title;

    @Column(name = "descricao", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "url_video", length = 254, nullable = false, unique = true)
    private String videoUrl;

    @Min(1)
    @Column(name = "ordem", nullable = false)
    private Integer position;

    @Min(0)
    @Column(name = "duracao_em_minutos")
    private Integer durationInMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    @ToString.Exclude
    private CourseModule module;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
