package com.huariservice.huariia.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cursos")
public class Cursos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 155,nullable = false,unique = true)
    private String titulos;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;
    @Column(name = "url_Video", length = 254, nullable = false, unique = true)
    private String urlVideo;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "autores_id",nullable = false)
    private Autores autores;

    public Cursos() {
    }

    public Cursos(String titulos, String descricao, String urlVideo, Categoria categoria, Autores autores) {
        this.titulos = titulos;
        this.descricao = descricao;
        this.urlVideo = urlVideo;
        this.categoria = categoria;
        this.autores = autores;
    }

    public Cursos(Long id, String titulos, String descricao, String urlVideo, Categoria categoria, Autores autores) {
        this.id = id;
        this.titulos = titulos;
        this.descricao = descricao;
        this.urlVideo = urlVideo;
        this.categoria = categoria;
        this.autores = autores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulos() {
        return titulos;
    }

    public void setTitulos(String titulos) {
        this.titulos = titulos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Autores getAutores() {
        return autores;
    }

    public void setAutores(Autores autores) {
        this.autores = autores;
    }
}
