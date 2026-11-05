package com.huariservice.huariia.entities;

import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.entities.enums.RoleConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * JPA entity representing a platform account (student or administrator) with JPA auditing and soft delete.
 */
@Entity
@Table(name = "usuarios")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE usuarios SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    @ToString.Include
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    @ToString.Include
    private String email;

    @Column(name = "senha_hash", length = 60, nullable = false)
    private String passwordHash;

    @Convert(converter = RoleConverter.class)
    @Column(name = "tipo_perfil", length = 20, columnDefinition = "varchar(20)", nullable = false)
    private Role role = Role.STUDENT;

    @CreatedDate
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = this.createdAt;
        normalizeEmail();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        normalizeEmail();
    }

    private void normalizeEmail() {
        if (email != null) {
            this.email = email.trim().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
