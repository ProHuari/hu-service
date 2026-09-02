package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.Modulo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AulasResponse {

    private Long id;
    private String titulos;
    private String descricao;
    private String urlVideo;
    private Integer ordem;
    private Integer duracaoEmMinutos;
    private Modulo modulo;
}
