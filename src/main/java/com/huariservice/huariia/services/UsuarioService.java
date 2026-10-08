package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.UsuarioRequest;
import com.huariservice.huariia.DTOs.UsuarioResponse;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Cadastro público: o perfil é sempre ALUNO (padrão da entidade).
    public UsuarioResponse pubUsuario(UsuarioRequest request) {
        String email = normalizar(request.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Esse e-mail já está cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> mostrarUsuarios() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(buscarUsuario(id));
    }

    public UsuarioResponse mudarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = buscarUsuario(id);

        String email = normalizar(request.email());
        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Esse e-mail já está cadastrado");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public void deletarId(Long id) {
        usuarioRepository.delete(buscarUsuario(id));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}