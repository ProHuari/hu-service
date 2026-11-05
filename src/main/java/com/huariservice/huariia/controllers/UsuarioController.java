package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.ChangePasswordRequest;
import com.huariservice.huariia.dto.ProfileRequest;
import com.huariservice.huariia.dto.UserRequest;
import com.huariservice.huariia.dto.UserResponse;
import com.huariservice.huariia.security.DadosToken;
import com.huariservice.huariia.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários")
@RequiredArgsConstructor
public class UsuarioController {

    private final UserService userService;

    // ---------- Perfil do próprio usuário (id vem do token) ----------

    @GetMapping("/me")
    @Operation(summary = "Mostra o perfil do usuário logado")
    public ResponseEntity<UserResponse> meuPerfil(
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(userService.findById(usuarioLogado.usuarioId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza nome e e-mail do usuário logado")
    public ResponseEntity<UserResponse> atualizarMeuPerfil(
            @Valid @RequestBody ProfileRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        return ResponseEntity.ok(userService.updateProfile(usuarioLogado.usuarioId(), request));
    }

    @PatchMapping("/me/senha")
    @Operation(summary = "Troca a senha do usuário logado (exige a senha atual)")
    public ResponseEntity<Void> trocarMinhaSenha(
            @Valid @RequestBody ChangePasswordRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal DadosToken usuarioLogado) {
        userService.changePassword(usuarioLogado.usuarioId(), request);
        return ResponseEntity.noContent().build();
    }

    // ---------- Cadastro ----------

    @PostMapping
    @Operation(summary = "Cadastra um usuário (perfil ALUNO)")
    public ResponseEntity<UserResponse> criar(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerStudent(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/admin")
    @Operation(summary = "Cadastra um administrador (somente ADMIN)")
    public ResponseEntity<UserResponse> criarAdmin(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.registerAdmin(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/usuarios/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    // ---------- Gestão de usuários (somente ADMIN) ----------

    @GetMapping
    @Operation(summary = "Lista os usuários (somente ADMIN)")
    public ResponseEntity<List<UserResponse>> listar() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário pelo id (somente ADMIN)")
    public ResponseEntity<UserResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário e redefine a senha (somente ADMIN)")
    public ResponseEntity<UserResponse> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um usuário (somente ADMIN)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
