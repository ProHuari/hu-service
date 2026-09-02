package com.huariservice.huariia.DTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoresRequest {

    @NotBlank(message = "O nome do canal é obrigatório")
    @Size(max = 100)
    private String nomeCanal;
    @NotBlank(message = "O link do canal é obrigatório")
    @Size(max = 254)
    private String linkCanal;
}
