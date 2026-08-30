package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modulos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Modulos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 150, nullable = false)
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descricao;
    @Column(nullable = false)
    private Integer ordem;
    @ManyToOne
    @JoinColumn(name = "cursos_id",nullable = false)
    private Cursos cursos;

}