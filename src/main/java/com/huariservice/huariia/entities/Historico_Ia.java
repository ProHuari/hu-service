package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historicoIA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historico_Ia {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT",nullable = false)
    private String pergunta;
    @Column(columnDefinition = "TEXT",nullable = false)
    private String resposta;
    @Column(name = "dataConsulta",nullable = false)
    private LocalDateTime dataConsulta;
    @ManyToOne
    @JoinColumn(name = "usuario_id",nullable = false)
    private Usuario usuario;

}
