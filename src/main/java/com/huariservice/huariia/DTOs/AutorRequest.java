package com.huariservice.huariia.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;


public record AutorRequest(

        @NotBlank(message = "O nome do canal é obrigatório")
        @Size(max = 100)
                String nomeCanal,

        @NotBlank(message = "O link do canal é obrigatório")
        @Size(max = 254)
        @URL(message = "Link do canal inválido")
        String linkCanal
) {}

