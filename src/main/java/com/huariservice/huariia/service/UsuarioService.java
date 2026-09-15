package com.huariservice.huariia.service;

import com.huariservice.huariia.DTOs.UsuarioRequest;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

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
}
