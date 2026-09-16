package com.huariservice.huariia.controller;

import com.huariservice.huariia.DTOs.UsuarioRequest;
import com.huariservice.huariia.DTOs.UsuarioResponse;
import com.huariservice.huariia.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.mostrarUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        String resposta = usuarioService.mudarUsuario(id, request);
        return ResponseEntity.ok(resposta);
    }
}
