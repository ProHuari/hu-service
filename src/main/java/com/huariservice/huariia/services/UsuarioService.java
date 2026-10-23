package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.PerfilRequest;
import com.huariservice.huariia.DTOs.SenhaRequest;
import com.huariservice.huariia.DTOs.UsuarioRequest;
import com.huariservice.huariia.DTOs.UsuarioResponse;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.entities.enums.TipoPerfil;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.exceptions.RegraNegocioException;
import com.huariservice.huariia.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    // Cadastro público: o perfil é sempre ALUNO.
    public UsuarioResponse pubUsuario(UsuarioRequest request) {
        return criar(request, TipoPerfil.ALUNO);
    }

    // Só ADMIN chega aqui (a regra fica no SecurityConfig).
    public UsuarioResponse pubAdmin(UsuarioRequest request) {
        return criar(request, TipoPerfil.ADMIN);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> mostrarUsuarios() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.from(buscarUsuario(id));
    }

    // Uso do ADMIN: altera nome, e-mail e redefine a senha de qualquer usuário.
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

    // Uso do próprio usuário (id vem do token): altera só nome e e-mail.
    public UsuarioResponse atualizarPerfil(Long id, PerfilRequest request) {
        Usuario usuario = buscarUsuario(id);

        String email = normalizar(request.email());
        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Esse e-mail já está cadastrado");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(email);

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    // Exige a senha atual: quem pegou um token emprestado não consegue trocar a senha.
    public void trocarSenha(Long id, SenhaRequest request) {
        Usuario usuario = buscarUsuario(id);

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw new RegraNegocioException("A senha atual está incorreta");
        }
        if (request.senhaAtual().equals(request.novaSenha())) {
            throw new RegraNegocioException("A nova senha deve ser diferente da atual");
        }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
    }

    public void deletarId(Long id) {
        usuarioRepository.delete(buscarUsuario(id));
    }

    private UsuarioResponse criar(UsuarioRequest request, TipoPerfil perfil) {
        String email = normalizar(request.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Esse e-mail já está cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setTipoPerfil(perfil);

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse usuário não foi cadastrado"));
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}