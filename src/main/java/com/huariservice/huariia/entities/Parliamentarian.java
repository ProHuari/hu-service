package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * JPA entity representing a federal parliamentarian (Deputy or Senator) for public transparency analysis.
 */
@Entity
@Table(name = "parlamentares", indexes = {
        @Index(name = "idx_parlamentar_partido_uf", columnList = "partido, uf")
})
@SQLDelete(sql = "UPDATE parlamentares SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parliamentarian extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "nome", length = 150, nullable = false)
    @ToString.Include
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "casa_legislativa", length = 30, nullable = false)
    private ParliamentarianChamber chamber;

    @Column(name = "partido", length = 30, nullable = false)
    private String party;

    @Column(name = "uf", length = 2, nullable = false)
    private String state;

    @Column(name = "id_externo", length = 60)
    private String externalId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parliamentarian other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

