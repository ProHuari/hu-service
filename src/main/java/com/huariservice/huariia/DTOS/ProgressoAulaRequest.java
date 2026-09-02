package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Aulas;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusAula;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressoAulaRequest {
    @NotNull(message = "O status é obrigatório")
    private StatusAula statusAula;
    private LocalDateTime conclusao;
    @NotNull(message = "O usuário é obrigatório")
    private Usuario usuario;
    @NotNull(message = "A aula é obrigatória")
    private Aulas aulas;
}
