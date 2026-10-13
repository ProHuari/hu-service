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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] CONTEUDO = {
            "/cursos/**", "/categorias/**", "/autores/**", "/modulos/**", "/aulas/**"
    };

    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // públicas
                        .requestMatchers("/auth/login", "/error").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        // usuários: só ADMIN (cadastro público acima é a exceção)
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMIN")

                        // conteúdo: qualquer logado lê, só ADMIN escreve
                        .requestMatchers(HttpMethod.GET, CONTEUDO).authenticated()
                        .requestMatchers(CONTEUDO).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        // perfil do próprio usuário: qualquer usuário logado
                        .requestMatchers("/usuarios/me", "/usuarios/me/**").authenticated()

                        // usuários: só ADMIN (cadastro público acima é a exceção)
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMIN")

                        // matrículas: alterar status e apagar só ADMIN
                        .requestMatchers(HttpMethod.PATCH, "/matriculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/matriculas/**").hasRole("ADMIN")

                        // o resto (matrícula, progresso, histórico): qualquer logado
                        .anyRequest().authenticated())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}