package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.TipoPerfil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Locale;


@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(length = 100, nullable = false)
    @ToString.Include
    private String nome;

    @Column(length = 100, nullable = false, unique = true)
    @ToString.Include
    private String email;

    // Hash BCrypt (60 caracteres). Nunca guardar a senha em texto puro.
    @Column(name = "senha_hash", length = 60, nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_perfil", nullable = false)
    private TipoPerfil tipoPerfil = TipoPerfil.ALUNO;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        normalizarEmail();
    }

    @PreUpdate
    void aoAtualizar() {
        normalizarEmail();
    }

    private void normalizarEmail() {
        if (email != null) {
            this.email = email.trim().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
