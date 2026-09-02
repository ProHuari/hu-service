package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.StatusAula;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "progresoAula")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgresoAula {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_aula",nullable = false)
    private StatusAula statusAula;
    @Column(nullable = false,name = "data_conclusao")
    private LocalDateTime conclusao;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id",nullable = false)
    private Usuario usuario;
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aulas aulas;

}
