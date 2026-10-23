package com.huariservice.huariia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
// Índices adicionados nas chaves estrangeiras (categoria_id e autor_id) para otimizar
// buscas e junções frequentes de cursos por categoria ou autor.
@Table(name = "cursos", indexes = {
        @Index(name = "idx_curso_categoria", columnList = "categoria_id"),
        @Index(name = "idx_curso_autor", columnList = "autor_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(length = 155, nullable = false, unique = true)
    @ToString.Include
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(name = "url_video", length = 254, nullable = false, unique = true)
    private String urlVideo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @ToString.Exclude
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    @ToString.Exclude
    private Autor autor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
