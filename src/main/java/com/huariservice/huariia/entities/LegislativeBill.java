package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * JPA entity representing a Legislative Bill (Projeto de Lei - PL) used for civic education and RAG analysis.
 */
@Entity
@Table(name = "projetos_lei")
@SQLDelete(sql = "UPDATE projetos_lei SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LegislativeBill extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    @ToString.Include
    private String code;

    @Column(name = "titulo", length = 200, nullable = false)
    @ToString.Include
    private String title;

    @Column(name = "ementa", columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "texto_integral", columnDefinition = "TEXT")
    private String fullText;

    @Column(name = "status_tramitacao", length = 100, nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parlamentar_autor_id")
    @ToString.Exclude
    private Parliamentarian author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LegislativeBill other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

