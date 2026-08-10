package com.huariservice.huariia.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "autores")
public class Autores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nomeCanal", length = 100, nullable = false, unique = true)
    private String nomeCanal;
    @Column(name = "linkCanal", length = 254, nullable = false, unique = true)
    private String linkCanal;

    public Autores() {
    }

    public Autores(String nomeCanal, String linkCanal) {
        this.nomeCanal = nomeCanal;
        this.linkCanal = linkCanal;
    }

    public Autores(Long id, String nomeCanal, String linkCanal) {
        this.id = id;
        this.nomeCanal = nomeCanal;
        this.linkCanal = linkCanal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCanal() {
        return nomeCanal;
    }

    public void setNomeCanal(String nomeCanal) {
        this.nomeCanal = nomeCanal;
    }

    public String getLinkCanal() {
        return linkCanal;
    }

    public void setLinkCanal(String linkCanal) {
        this.linkCanal = linkCanal;
    }
}