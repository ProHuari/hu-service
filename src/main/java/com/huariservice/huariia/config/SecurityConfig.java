package com.huariservice.huariia.config;

import com.huariservice.huariia.security.JwtAuthenticationFilter;
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

/**
 * Stateless JWT security configuration and route authorization rules.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        /** Catalog routes: any authenticated user can read, only ADMIN can write. */
        private static final String[] CATALOG_ROUTES = {
                        "/courses", "/courses/**",
                        "/categories", "/categories/**",
                        "/authors", "/authors/**",
                        "/modules", "/modules/**",
                        "/lessons", "/lessons/**"
        };

        private static final String UNAUTHORIZED_BODY = """
                        {"status":401,"error":"Não autorizado","message":"Token ausente, inválido ou expirado."}""";

        private static final String FORBIDDEN_BODY = """
                        {"status":403,"error":"Acesso negado","message":"Você não tem permissão para acessar este recurso."}""";

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        /**
         * Builds the security filter chain: CSRF disabled, CORS enabled, stateless
         * sessions,
         * JSON 401/403 responses and role-based route rules.
         *
         * @param http the HTTP security builder
         * @return the configured filter chain
         * @throws Exception if the configuration fails
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(e -> e
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.getWriter().write(UNAUTHORIZED_BODY);
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setStatus(HttpStatus.FORBIDDEN.value());
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.getWriter().write(FORBIDDEN_BODY);
                                                }))
                                .authorizeHttpRequests(auth -> auth
                                                // public
                                                .requestMatchers("/auth/login", "/error").permitAll()
                                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                                // own profile: any authenticated user
                                                .requestMatchers("/users/me", "/users/me/**").authenticated()
                                                // user management: ADMIN only (public sign-up above is the exception)
                                                .requestMatchers("/users", "/users/**").hasRole("ADMIN")
                                                // catalog: any authenticated user reads, only ADMIN writes
                                                .requestMatchers(HttpMethod.GET, CATALOG_ROUTES).authenticated()
                                                .requestMatchers(CATALOG_ROUTES).hasRole("ADMIN")
                                                // enrollments: changing status and deleting are ADMIN only
                                                .requestMatchers(HttpMethod.PATCH, "/enrollments/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/enrollments/**").hasRole("ADMIN")
                                                // everything else (enrollment, progress, AI history): any authenticated
                                                // user
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
