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

/**
 * Creates the initial administrator on startup, when {@code ADMIN_EMAIL} and
 * {@code ADMIN_SENHA} are defined and no user with that e-mail exists yet.
 */
@Slf4j
@Component
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String name;
    private final String email;
    private final String password;

    /**
     * Creates the initializer.
     *
     * @param userRepository  user data access
     * @param passwordEncoder password hasher
     * @param name            admin name, from {@code app.admin.nome}
     * @param email           admin e-mail, from {@code app.admin.email} (blank disables the initializer)
     * @param password        admin password, from {@code app.admin.senha} (blank disables the initializer)
     */
    public AdminInitializer(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.nome}") String name,
                            @Value("${app.admin.email}") String email,
                            @Value("${app.admin.senha}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Creates the admin user if configured and not yet present.
     *
     * @param args application arguments (unused)
     * @throws IllegalStateException if the configured password is not between 8 and 72 characters
     */
    @Override
    public void run(ApplicationArguments args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.info("ADMIN_EMAIL/ADMIN_SENHA not set: no initial admin will be created.");
            return;
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.info("Initial admin already exists ({}). Nothing to do.", normalizedEmail);
            return;
        }

        if (password.length() < 8 || password.length() > 72) {
            throw new IllegalStateException("ADMIN_SENHA must be between 8 and 72 characters.");
        }

        User admin = new User();
        admin.setName(name);
        admin.setEmail(normalizedEmail);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        log.info("Initial admin created: {}", normalizedEmail);
    }
}
