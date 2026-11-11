package com.huariservice.huariia.config;

import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.entities.enums.Role;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@Order(1)
public class AdminInitializer implements ApplicationRunner {

    private static final String DEFAULT_ADMIN_NAME = "Administrador";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@huari.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "senha123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.nome:Administrador}") String adminName,
                            @Value("${app.admin.email:}") String adminEmail,
                            @Value("${app.admin.senha:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        String targetName = (adminName == null || adminName.isBlank()) ? DEFAULT_ADMIN_NAME : adminName.trim();
        String targetEmail = adminEmail;
        String targetPassword = adminPassword;

        if (targetEmail == null || targetEmail.isBlank() || targetPassword == null || targetPassword.isBlank()) {
            if (userRepository.count() == 0) {
                targetEmail = DEFAULT_ADMIN_EMAIL;
                targetPassword = DEFAULT_ADMIN_PASSWORD;
                log.info("ADMIN_EMAIL/ADMIN_SENHA not set and database is empty: seeding default admin ({}).",
                        DEFAULT_ADMIN_EMAIL);
            } else {
                log.info("ADMIN_EMAIL/ADMIN_SENHA not set: skipping initial admin creation.");
                return;
            }
        }

        String normalizedEmail = targetEmail.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.info("Initial admin already exists ({}). Nothing to do.", normalizedEmail);
            return;
        }

        if (targetPassword.length() < 8 || targetPassword.length() > 72) {
            throw new IllegalStateException("ADMIN_SENHA must be between 8 and 72 characters.");
        }

        User admin = new User();
        admin.setName(targetName);
        admin.setEmail(normalizedEmail);
        admin.setPasswordHash(passwordEncoder.encode(targetPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        log.info("Initial admin created: {}", normalizedEmail);
    }
}

