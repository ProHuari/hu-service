package com.huariservice.huariia.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "aulas")
public class Aulas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 155,nullable = false,unique = true)
    private String titulos;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;
    @Column(name = "url_Video", length = 254, nullable = false, unique = true)
    private String urlVideo;
    @Column(nullable = false)
    private Integer ordem;

    @Column(name = "duracao_em_minutos")
    private Integer duracaoEmMinutos;

    @ManyToOne
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulos modulos;

    public Aulas() {
    }

    public Aulas( String titulos, String descricao, String urlVideo, Integer ordem, Integer duracaoEmMinutos, Modulos modulos) {
        this.titulos = titulos;
        this.descricao = descricao;
        this.urlVideo = urlVideo;
        this.ordem = ordem;
        this.duracaoEmMinutos = duracaoEmMinutos;
        this.modulos = modulos;
    }

    public Aulas(Long id, String titulos, String descricao, String urlVideo, Integer ordem, Integer duracaoEmMinutos, Modulos modulos) {
        this.id = id;
        this.titulos = titulos;
        this.descricao = descricao;
        this.urlVideo = urlVideo;
        this.ordem = ordem;
        this.duracaoEmMinutos = duracaoEmMinutos;
        this.modulos = modulos;
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

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getDuracaoEmMinutos() {
        return duracaoEmMinutos;
    }

    public void setDuracaoEmMinutos(Integer duracaoEmMinutos) {
        this.duracaoEmMinutos = duracaoEmMinutos;
    }

    public Modulos getModulos() {
        return modulos;
    }

    public void setModulos(Modulos modulos) {
        this.modulos = modulos;
    }
}