package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.StatusMT;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "matriculas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Matriculas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,name = "data_matricula")
    private LocalDate dtMatricula;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "statusMT")
    private StatusMT statusMT;
    @ManyToOne
    @JoinColumn(name = "usuario_id",nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "cursos_id",nullable = false)
    private Cursos cursos;

}
