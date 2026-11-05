package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.LegislativeBillRequest;
import com.huariservice.huariia.dto.LegislativeBillResponse;
import com.huariservice.huariia.dto.ParliamentarianExpenseRequest;
import com.huariservice.huariia.dto.ParliamentarianExpenseResponse;
import com.huariservice.huariia.dto.ParliamentarianRequest;
import com.huariservice.huariia.dto.ParliamentarianResponse;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import com.huariservice.huariia.services.PoliticalTransparencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transparencia")
@Tag(name = "Transparência Política & Educação Cidadã")
@RequiredArgsConstructor
public class PoliticalTransparencyController {

    private final PoliticalTransparencyService transparencyService;

    // ---------- Parlamentares (Deputados e Senadores) ----------

    @PostMapping("/parlamentares")
    @Operation(summary = "Cadastra um parlamentar (Deputado ou Senador)")
    public ResponseEntity<ParliamentarianResponse> createParliamentarian(
            @Valid @RequestBody ParliamentarianRequest request) {
        ParliamentarianResponse response = transparencyService.createParliamentarian(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/parlamentares")
    @Operation(summary = "Lista parlamentares (filtro opcional por casa legislativa)")
    public ResponseEntity<List<ParliamentarianResponse>> listParliamentarians(
            @RequestParam(required = false) ParliamentarianChamber chamber) {
        return ResponseEntity.ok(transparencyService.listParliamentarians(chamber));
    }

    @GetMapping("/parlamentares/{id}")
    @Operation(summary = "Busca um parlamentar pelo id")
    public ResponseEntity<ParliamentarianResponse> getParliamentarian(@PathVariable Long id) {
        return ResponseEntity.ok(transparencyService.findParliamentarianById(id));
    }

    @DeleteMapping("/parlamentares/{id}")
    @Operation(summary = "Remove (soft-delete) um parlamentar")
    public ResponseEntity<Void> deleteParliamentarian(@PathVariable Long id) {
        transparencyService.deleteParliamentarian(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Projetos de Lei (PLs) ----------

    @PostMapping("/projetos-lei")
    @Operation(summary = "Cadastra um Projeto de Lei (PL)")
    public ResponseEntity<LegislativeBillResponse> createBill(
            @Valid @RequestBody LegislativeBillRequest request) {
        LegislativeBillResponse response = transparencyService.createBill(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/projetos-lei")
    @Operation(summary = "Lista Projetos de Lei (filtro opcional por termo de busca)")
    public ResponseEntity<List<LegislativeBillResponse>> listBills(
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(transparencyService.listBills(q));
    }

    @GetMapping("/projetos-lei/{id}")
    @Operation(summary = "Busca um Projeto de Lei pelo id")
    public ResponseEntity<LegislativeBillResponse> getBill(@PathVariable Long id) {
        return ResponseEntity.ok(transparencyService.findBillById(id));
    }

    @DeleteMapping("/projetos-lei/{id}")
    @Operation(summary = "Remove (soft-delete) um Projeto de Lei")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        transparencyService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Despesas Parlamentares (CEAP) ----------

    @PostMapping("/despesas")
    @Operation(summary = "Registra uma despesa parlamentar (CEAP)")
    public ResponseEntity<ParliamentarianExpenseResponse> createExpense(
            @Valid @RequestBody ParliamentarianExpenseRequest request) {
        ParliamentarianExpenseResponse response = transparencyService.createExpense(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/despesas")
    @Operation(summary = "Lista despesas parlamentares (filtro opcional por parlamentarId)")
    public ResponseEntity<List<ParliamentarianExpenseResponse>> listExpenses(
            @RequestParam(required = false) Long parlamentarId) {
        return ResponseEntity.ok(transparencyService.listExpenses(parlamentarId));
    }

    @DeleteMapping("/despesas/{id}")
    @Operation(summary = "Remove (soft-delete) uma despesa parlamentar")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        transparencyService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}

