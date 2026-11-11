package com.huariservice.huariia.config;

import com.huariservice.huariia.entities.*;
import com.huariservice.huariia.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@Order(2)
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            log.info("[DevDataSeeder] Banco de dados já populado. Nenhuma ação necessária.");
            return;
        }

        log.info("[DevDataSeeder] Iniciando população de dados de teste...");

        // 1. Usuário Aluno de Teste (Ajuste os nomes dos setters conforme a sua classe User atual)
        if (!userRepository.existsByEmail("aluno@huari.com")) {
            User aluno = new User();
            aluno.setName("Aluno Exemplo");
            aluno.setEmail("aluno@huari.com");
            aluno.setPasswordHash(passwordEncoder.encode("aluno123456"));
            // aluno.setRole(...); // Defina o Enum de Role correto
            userRepository.save(aluno);
        }

        // 2. Categorias
        Category catDesenvolvimento = new Category(null, "Desenvolvimento Web", "Cursos de programação web e APIs");
        categoryRepository.save(catDesenvolvimento);

        // 3. Autores
        Author autor1 = new Author(null, "Huari Academy", "https://youtube.com/@huariacademy");
        authorRepository.save(autor1);

        // 4. Cursos
        Course curso1 = new Course(
                null,
                "Introdução ao Spring Boot e Java 25",
                "Domine o desenvolvimento de APIs REST modernas com Spring Boot.",
                "https://www.youtube.com/watch?v=intro-spring",
                catDesenvolvimento,
                autor1
        );
        courseRepository.save(curso1);

        log.info("[DevDataSeeder] População concluída com sucesso!");
    }
}