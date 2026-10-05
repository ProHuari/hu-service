package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Aula;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.StatusAula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressoAulaResponse {

    private Long id;
    private StatusAula statusAula;
    private LocalDateTime conclusao;
    private Usuario usuario;
    private Aula aula;

}
