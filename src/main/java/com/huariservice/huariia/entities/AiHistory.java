package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * JPA entity that stores each question a {@link User} asked the AI assistant together with
 * the generated answer.
 */
@Entity
@Table(name = "historico_ia", indexes = {
        @Index(name = "idx_historico_ia_usuario_data", columnList = "usuario_id, data_consulta")
})
@SQLDelete(sql = "UPDATE historico_ia SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiHistory extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "pergunta", columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(name = "resposta", columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Column(name = "data_consulta", nullable = false, updatable = false)
    private LocalDateTime queriedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private User user;

    @PrePersist
    void onCreate() {
        if (queriedAt == null) {
            this.queriedAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AiHistory other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
