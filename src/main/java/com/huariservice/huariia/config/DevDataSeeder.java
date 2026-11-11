package com.huariservice.huariia.config;

import com.huariservice.huariia.entities.*;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            log.info("[DevDataSeeder] Banco de dados já populado. Nenhuma ação necessária.");
            return;
        }

        log.info("[DevDataSeeder] Iniciando população de dados de teste...");

        // 1. Usuário Aluno de Teste
        if (!userRepository.existsByEmail("aluno@huari.com")) {
            User aluno = new User();
            aluno.setName("Aluno Exemplo");
            aluno.setEmail("aluno@huari.com");
            aluno.setPasswordHash(passwordEncoder.encode("aluno123456"));
            aluno.setRole(Role.STUDENT);
            userRepository.save(aluno);
            log.info("[DevDataSeeder] Usuário aluno criado: aluno@huari.com / aluno123456");
        }

        // 2. Categoria
        Category catDesenvolvimento = new Category(null, "Desenvolvimento Web", "Cursos de programação web e APIs");
        catDesenvolvimento = categoryRepository.save(catDesenvolvimento);

        // 3. Autor
        Author autor1 = new Author(null, "Huari Academy", "https://youtube.com/@huariacademy");
        autor1 = authorRepository.save(autor1);

        // 4. Curso
        Course curso1 = new Course(
                null,
                "Introdução ao Spring Boot e Java 25",
                "Domine o desenvolvimento de APIs REST modernas com Spring Boot.",
                "https://www.youtube.com/watch?v=intro-spring",
                catDesenvolvimento,
                autor1
        );
        curso1 = courseRepository.save(curso1);

        // 5. Módulo do Curso
        CourseModule modulo1 = new CourseModule(
                null,
                "Módulo 1: Fundamentos do Spring Boot",
                "Conceitos básicos, injeção de dependência e arquitetura REST.",
                1,
                curso1
        );
        modulo1 = courseModuleRepository.save(modulo1);

        // 6. Aula do Módulo
        Lesson aula1 = new Lesson(
                null,
                "Aula 1: Setup do Ambiente e Java 25",
                "Instalação do JDK 25 e inicialização do projeto via Spring Initializr.",
                "https://www.youtube.com/watch?v=setup-ambiente",
                1,
                15,
                modulo1
        );
        lessonRepository.save(aula1);

        log.info("[DevDataSeeder] População concluída com sucesso! (1 Curso, 1 Módulo, 1 Aula cadastrados)");
    }
}
