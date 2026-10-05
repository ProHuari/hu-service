package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "aulas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"titulos", "modulo_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(length = 155, nullable = false)
    @ToString.Include
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(name = "url_video", length = 254, nullable = false, unique = true)
    private String urlVideo;

    @Min(1)
    @Column(nullable = false)
    private Integer ordem;

    @Min(0)
    @Column(name = "duracao_em_minutos")
    private Integer duracaoEmMinutos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aula other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}