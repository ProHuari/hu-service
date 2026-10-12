package com.huariservice.huariia.DTOs;

public record LoginResponse(String token, String tipo, Long usuarioId, String perfil) {

    public static LoginResponse of(String token, Long usuarioId, String perfil) {
        return new LoginResponse(token, "Bearer", usuarioId, perfil);
    }
}
