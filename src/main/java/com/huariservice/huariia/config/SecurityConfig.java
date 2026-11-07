package com.huariservice.huariia.config;

import com.huariservice.huariia.security.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_READ_CONTENT_ENDPOINTS = {
            "/cursos", "/cursos/**",
            "/categorias", "/categorias/**",
            "/autores", "/autores/**",
            "/modulos", "/modulos/**",
            "/transparencia", "/transparencia/**"
    };

    private static final String[] ADMIN_CONTENT_ENDPOINTS = {
            "/cursos", "/cursos/**",
            "/categorias", "/categorias/**",
            "/autores", "/autores/**",
            "/modulos", "/modulos/**",
            "/aulas", "/aulas/**",
            "/transparencia", "/transparencia/**"
    };

    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'"))
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .frameOptions(frame -> frame.deny()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                    {"status":401,"error":"Unauthorized","message":"Missing, invalid, or expired token."}""");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                                    {"status":403,"error":"Forbidden","message":"You do not have permission to access this resource."}""");
                        }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/logout",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/actuator/health",
                                "/actuator/info",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        .requestMatchers("/usuarios/me", "/usuarios/me/**").authenticated()
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, PUBLIC_READ_CONTENT_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/aulas", "/aulas/**").authenticated()
                        .requestMatchers(ADMIN_CONTENT_ENDPOINTS).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/matriculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/matriculas/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
