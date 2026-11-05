package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity representing a public expense (CEAP / Cota Parlamentar) of a {@link Parliamentarian}.
 */
@Entity
@Table(name = "despesas_parlamentares", indexes = {
        @Index(name = "idx_despesa_parlamentar", columnList = "parlamentar_id")
})
@SQLDelete(sql = "UPDATE despesas_parlamentares SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParliamentarianExpense extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parlamentar_id", nullable = false)
    @ToString.Exclude
    private Parliamentarian parliamentarian;

    @Column(name = "categoria_despesa", length = 150, nullable = false)
    private String expenseCategory;

    @Column(name = "fornecedor", length = 200, nullable = false)
    private String supplierName;

    @Column(name = "valor", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "data_despesa", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "url_documento", length = 254)
    private String documentUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParliamentarianExpense other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

