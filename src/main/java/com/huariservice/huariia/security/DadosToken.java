package com.huariservice.huariia.security;

public record DadosToken(Long usuarioId, String email, String perfil) {

    public boolean isAdmin() {
        return "ADMIN".equals(perfil);
    }
}