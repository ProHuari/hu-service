package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "autores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Autores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "nome_canal", length = 100, nullable = false, unique = true)
    @ToString.Include
    private String nomeCanal;

    @Column(name = "link_canal", length = 254, nullable = false, unique = true)
    private String linkCanal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Autores other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}