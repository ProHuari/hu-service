package com.huariservice.huariia.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "modulos")
public class Modulos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Modulos() {
    }

    public Modulos(String titulo, String descricao, Integer ordem, Cursos cursos) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.ordem = ordem;
        this.cursos = cursos;
    }

    public Modulos(Long id, String titulo, String descricao, Integer ordem, Cursos cursos) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.ordem = ordem;
        this.cursos = cursos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Cursos getCursos() {
        return cursos;
    }

    public void setCursos(Cursos cursos) {
        this.cursos = cursos;
    }
}


