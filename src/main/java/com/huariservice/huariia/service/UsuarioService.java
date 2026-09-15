package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.UsuarioRequest;
import com.huariservice.huariia.DTOs.UsuarioResponse;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioRequest pubUsuario(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setTipoPerfil(request.getTipoPerfil());

        usuarioRepository.save(usuario);
        return request;
    }

    public List<UsuarioResponse> mostrarUsuarios() {
        return usuarioRepository.findAll().stream().map(usuario -> new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getTipoPerfil()
        )).toList();
    }

    public String mudarUsuario(Long id, UsuarioRequest usuarioAlterado) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();

        usuario.setNome(usuarioAlterado.getNome());
        usuario.setEmail(usuarioAlterado.getEmail());
        usuario.setSenha(usuarioAlterado.getSenha());
        usuario.setTipoPerfil(usuarioAlterado.getTipoPerfil());

        usuarioRepository.save(usuario);
        return "Usuário alterado";
    }

    public String deletarId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isEmpty()) {
            return "Esse usuário não foi cadastrado";
        } else {
            usuarioRepository.deleteById(id);
            return "Usuário excluído";
        }
    }
}
