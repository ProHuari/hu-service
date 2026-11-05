package com.huariservice.huariia.config;

import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
public class AdminInicializador implements ApplicationRunner {

    private static final String DEFAULT_ADMIN_NAME = "Administrador";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@huari.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "senha123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String nome;
    private final String email;
    private final String senha;

    public AdminInicializador(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.admin.nome:Administrador}") String nome,
                              @Value("${app.admin.email:}") String email,
                              @Value("${app.admin.senha:}") String senha) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        String targetName = (nome == null || nome.isBlank()) ? DEFAULT_ADMIN_NAME : nome.trim();
        String targetEmail = email;
        String targetPassword = senha;

        if (targetEmail == null || targetEmail.isBlank() || targetPassword == null || targetPassword.isBlank()) {
            if (userRepository.count() == 0) {
                targetEmail = DEFAULT_ADMIN_EMAIL;
                targetPassword = DEFAULT_ADMIN_PASSWORD;
                log.info("ADMIN_EMAIL/ADMIN_SENHA não definidos e banco vazio: criando admin padrão ({}).",
                        DEFAULT_ADMIN_EMAIL);
            } else {
                log.info("ADMIN_EMAIL/ADMIN_SENHA não definidos: nenhum admin inicial será criado.");
                return;
            }
        }

        String emailNormalizado = targetEmail.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(emailNormalizado)) {
            log.info("Admin inicial já existe ({}). Nada a fazer.", emailNormalizado);
            return;
        }

        if (targetPassword.length() < 8 || targetPassword.length() > 72) {
            throw new IllegalStateException("ADMIN_SENHA deve ter entre 8 e 72 caracteres.");
        }

        User admin = new User();
        admin.setName(targetName);
        admin.setEmail(emailNormalizado);
        admin.setPasswordHash(passwordEncoder.encode(targetPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        log.info("Admin inicial criado: {}", emailNormalizado);
    }
}
