package com.huariservice.huariia.security;

public record TokenData(Long userId, String email, String role) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
