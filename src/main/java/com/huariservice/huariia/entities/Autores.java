package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "autores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Autores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nomeCanal", length = 100, nullable = false, unique = true)
    private String nomeCanal;
    @Column(name = "linkCanal", length = 254, nullable = false, unique = true)
    private String linkCanal;
}