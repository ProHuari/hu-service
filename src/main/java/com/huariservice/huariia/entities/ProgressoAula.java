package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.StatusAula;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "progresso_aula", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "aula_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressoAula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aula", nullable = false)
    @ToString.Include
    private StatusAula statusAula = StatusAula.NAO_INICIADA;

    // Preenchida apenas quando statusAula == CONCLUIDA
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    public void concluir() {
        this.statusAula = StatusAula.CONCLUIDA;
        this.dataConclusao = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgressoAula other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
