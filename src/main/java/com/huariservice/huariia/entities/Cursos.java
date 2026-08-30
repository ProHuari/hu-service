package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cursos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 155,nullable = false,unique = true)
    private String titulos;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;
    @Column(name = "url_Video", length = 254, nullable = false, unique = true)
    private String urlVideo;
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categorias categoria;
    @ManyToOne
    @JoinColumn(name = "autores_id",nullable = false)
    private Autores autores;

}