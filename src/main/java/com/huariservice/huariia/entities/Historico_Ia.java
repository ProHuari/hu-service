package com.huariservice.huariia.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historicoIA")
public class Historico_Ia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Historico_Ia() {
    }

    public Historico_Ia( String pergunta, String resposta, LocalDateTime dataConsulta, Usuario usuario) {
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.dataConsulta = dataConsulta;
        this.usuario = usuario;
    }

    public Historico_Ia(Long id, String pergunta, String resposta, LocalDateTime dataConsulta, Usuario usuario) {
        this.id = id;
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.dataConsulta = dataConsulta;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
