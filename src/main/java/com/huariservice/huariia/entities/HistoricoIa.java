package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_ia", indexes = {
        @Index(name = "idx_historico_ia_usuario_data", columnList = "usuario_id, data_consulta")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoIa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String pergunta;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resposta;

    @Column(name = "data_consulta", nullable = false, updatable = false)
    private LocalDateTime dataConsulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    @PrePersist
    void aoCriar() {
        if (dataConsulta == null) {
            this.dataConsulta = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoricoIa other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
