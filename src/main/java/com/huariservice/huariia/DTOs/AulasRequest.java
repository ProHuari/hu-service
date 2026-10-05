package com.huariservice.huariia.DTOs;

import com.huariservice.huariia.entities.Modulo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;


public record AulasRequest(
@NotBlank(message = "O título é obrigatório")
@Size(max = 155)
String titulo,

@NotBlank(message = "A descrição é obrigatória")
String descricao,

@NotBlank(message = "A URL do vídeo é obrigatória")
@Size(max = 254)
@URL(message = "URL do vídeo inválida")
String urlVideo,

@NotNull(message = "A ordem é obrigatória")
@Min(value = 1, message = "A ordem deve ser no mínimo 1")
Integer ordem,

@Min(value = 0, message = "A duração não pode ser negativa")
Integer duracaoEmMinutos,

@NotNull(message = "O módulo é obrigatório")
Long moduloId
) {}
