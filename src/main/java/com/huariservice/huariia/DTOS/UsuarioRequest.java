package com.huariservice.huariia.DTOS;

import com.huariservice.huariia.entities.enums.TipoPerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 1, max = 100, message = "O nome deve ter entre 1 e 100 caracteres")
    private String nome;
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 100)
    private String email;
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;
    private TipoPerfil tipoPerfil;
}
