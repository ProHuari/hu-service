package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.UsuarioRequest;
import com.huariservice.huariia.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    @PostMapping
    public ResponseEntity<UsuarioRequest> criarUsuario(@RequestBody UsuarioRequest request) {
        UsuarioRequest novoUsuario = usuarioService.pubUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }
}
