package com.huariservice.huariia.config;

import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.TipoPerfil;
import com.huariservice.huariia.repositories.UsuarioRepository;
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

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String nome;
    private final String email;
    private final String senha;

    public AdminInicializador(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.admin.nome}") String nome,
                              @Value("${app.admin.email}") String email,
                              @Value("${app.admin.senha}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            log.info("ADMIN_EMAIL/ADMIN_SENHA não definidos: nenhum admin inicial será criado.");
            return;
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            log.info("Admin inicial já existe ({}). Nada a fazer.", emailNormalizado);
            return;
        }

        if (senha.length() < 8 || senha.length() > 72) {
            throw new IllegalStateException("ADMIN_SENHA deve ter entre 8 e 72 caracteres.");
        }

        Usuario admin = new Usuario();
        admin.setNome(nome);
        admin.setEmail(emailNormalizado);
        admin.setSenhaHash(passwordEncoder.encode(senha));
        admin.setTipoPerfil(TipoPerfil.ADMIN);
        usuarioRepository.save(admin);

        log.info("Admin inicial criado: {}", emailNormalizado);
    }
}
