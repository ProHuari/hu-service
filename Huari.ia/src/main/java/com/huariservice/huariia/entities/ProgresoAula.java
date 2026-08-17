package com.huariservice.huariia.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "progresoAula")
public class ProgresoAula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_aula",nullable = false)
    private StatusAula statusAula;
    @Column(nullable = false,name = "data_conclusao")
    private LocalDateTime conclusao;

    @ManyToOne
    @JoinColumn(name = "usuario_id",nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "aula_id", nullable = false)
    private Aulas aulas;

    public ProgresoAula() {
    }

    public ProgresoAula( StatusAula statusAula, LocalDateTime conclusao, Usuario usuario, Aulas aulas) {
        this.statusAula = statusAula;
        this.conclusao = conclusao;
        this.usuario = usuario;
        this.aulas = aulas;
    }

    public ProgresoAula(Long id, StatusAula statusAula, LocalDateTime conclusao, Usuario usuario, Aulas aulas) {
        this.id = id;
        this.statusAula = statusAula;
        this.conclusao = conclusao;
        this.usuario = usuario;
        this.aulas = aulas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusAula getStatusAula() {
        return statusAula;
    }

    public void setStatusAula(StatusAula statusAula) {
        this.statusAula = statusAula;
    }

    public LocalDateTime getConclusao() {
        return conclusao;
    }

    public void setConclusao(LocalDateTime conclusao) {
        this.conclusao = conclusao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Aulas getAulas() {
        return aulas;
    }

    public void setAulas(Aulas aulas) {
        this.aulas = aulas;
    }
}
