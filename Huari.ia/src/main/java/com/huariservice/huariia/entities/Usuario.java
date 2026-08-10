package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 1, max = 100, message = "O nome deve ter entre 1 e 100 caracteres")
    @Column(length = 100,nullable = false)
    private String nome;
    @Column(length = 100,nullable = false, unique = true)
    private String email;
    @Size(min = 1, max = 50, message = "A senha deve ter entre 1 e 50 caracteres")
    @Column(length = 50, nullable = false)
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo_Perfil")
    private TipoPerfil tipoPerfil;

    public Usuario() {
    }

    public Usuario(String email, String nome, String senha, TipoPerfil tipoPerfil) {
        this.email = email;
        this.nome = nome;
        this.senha = senha;
        this.tipoPerfil = tipoPerfil;
    }

    public Usuario(long id, String nome, String email, String senha, TipoPerfil tipoPerfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoPerfil = tipoPerfil;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }
}
