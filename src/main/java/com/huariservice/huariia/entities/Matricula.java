package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.StatusMatricula;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "matriculas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "cursos_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Matricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "data_matricula", nullable = false, updatable = false)
    private LocalDate dataMatricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_matricula", nullable = false)
    @ToString.Include
    private StatusMatricula statusMatricula = StatusMatricula.ATIVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Cursos curso;

    @PrePersist
    void aoCriar() {
        if (dataMatricula == null) {
            this.dataMatricula = LocalDate.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Matricula other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
