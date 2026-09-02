package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "aulas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aulas {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

}