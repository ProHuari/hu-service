package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}
