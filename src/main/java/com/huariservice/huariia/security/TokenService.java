package com.huariservice.huariia.security;

import com.huariservice.huariia.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    private final SecretKey chave;
    private final long expiracaoMinutos;
    private final long refreshExpiracaoMinutos;

    public TokenService(String secret, long expiracaoMinutos) {
        this(secret, expiracaoMinutos, 10080L);
    }

    @Autowired
    public TokenService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes:120}") long expiracaoMinutos,
            @Value("${app.jwt.refresh-expiration-minutes:10080}") long refreshExpiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
        this.refreshExpiracaoMinutos = refreshExpiracaoMinutos;
    }

    public String gerarToken(User usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("perfil", usuario.getRole().name())
                .claim("type", "access")
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }

    public String gerarRefreshToken(User usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("type", "refresh")
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(refreshExpiracaoMinutos, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }

    public Optional<DadosToken> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if ("refresh".equals(claims.get("type", String.class))) {
                return Optional.empty();
            }

            return Optional.of(new DadosToken(
                    Long.valueOf(claims.getSubject()),
                    claims.get("email", String.class),
                    claims.get("perfil", String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<Long> validarRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            if (!"refresh".equals(claims.get("type", String.class))) {
                return Optional.empty();
            }

            return Optional.of(Long.valueOf(claims.getSubject()));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<Instant> extrairExpiracao(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            return expiration != null ? Optional.of(expiration.toInstant()) : Optional.empty();
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
