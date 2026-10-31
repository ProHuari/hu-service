package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.PerfilRequest;
import com.huariservice.huariia.dto.SenhaRequest;
import com.huariservice.huariia.dto.UsuarioRequest;
import com.huariservice.huariia.dto.UsuarioResponse;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;


    // ---------- Perfil do próprio usuário (id vem do token) ----------

    @GetMapping("/me")
    @Operation(summary = "Mostra o perfil do usuário logado")
    public ResponseEntity<UsuarioResponse> meuPerfil(
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(usuarioService.buscarPorId(usuarioLogado.usuarioId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza nome e e-mail do usuário logado")
    public ResponseEntity<UsuarioResponse> atualizarMeuPerfil(
            @Valid @RequestBody PerfilRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(usuarioLogado.usuarioId(), request));
    }

    @PatchMapping("/me/senha")
    @Operation(summary = "Troca a senha do usuário logado (exige a senha atual)")
    public ResponseEntity<Void> trocarMinhaSenha(
            @Valid @RequestBody SenhaRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        usuarioService.trocarSenha(usuarioLogado.usuarioId(), request);
        return ResponseEntity.noContent().build();
    }

    // ---------- Cadastro ----------

    @PostMapping
    @Operation(summary = "Cadastra um usuário (perfil ALUNO)")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.pubUsuario(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/admin")
    @Operation(summary = "Cadastra um administrador (somente ADMIN)")
    public ResponseEntity<UsuarioResponse> criarAdmin(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.pubAdmin(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/usuarios/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    // ---------- Gestão de usuários (somente ADMIN) ----------

    @GetMapping
    @Operation(summary = "Lista os usuários (somente ADMIN)")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.mostrarUsuarios());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário pelo id (somente ADMIN)")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário e redefine a senha (somente ADMIN)")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.mudarUsuario(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um usuário (somente ADMIN)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletarId(id);
        return ResponseEntity.noContent().build();
    }
}