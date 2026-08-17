package com.huariservice.huariia.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "matriculas")
public class Matriculas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,name = "data da matricula")
    private LocalDate dtMatricula;
    @Column(nullable = false)
    private StatusMT statusMT;
    @ManyToOne
    @JoinColumn(name = "usuario_id",nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "Cursos_id",nullable = false)
    private Cursos cursos;

    public Matriculas() {
    }

    public Matriculas( LocalDate dtMatricula, StatusMT statusMT, Usuario usuario, Cursos cursos) {
        this.dtMatricula = dtMatricula;
        this.statusMT = statusMT;
        this.usuario = usuario;
        this.cursos = cursos;
    }

    public Matriculas(Long id, LocalDate dtMatricula, StatusMT statusMT, Usuario usuario, Cursos cursos) {
        this.id = id;
        this.dtMatricula = dtMatricula;
        this.statusMT = statusMT;
        this.usuario = usuario;
        this.cursos = cursos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDtMatricula() {
        return dtMatricula;
    }

    public void setDtMatricula(LocalDate dtMatricula) {
        this.dtMatricula = dtMatricula;
    }

    public StatusMT getStatusMT() {
        return statusMT;
    }

    public void setStatusMT(StatusMT statusMT) {
        this.statusMT = statusMT;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cursos getCursos() {
        return cursos;
    }

    public void setCursos(Cursos cursos) {
        this.cursos = cursos;
    }
}
