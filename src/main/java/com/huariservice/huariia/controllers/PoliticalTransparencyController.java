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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/transparencia")
@Tag(name = "Political Transparency & Civic Education", description = "Public data on Deputies, Senators, Legislative Bills (PLs), and Parliamentary Expenses (CEAP)")
@RequiredArgsConstructor
public class PoliticalTransparencyController {

    private final PoliticalTransparencyService transparencyService;

    @PostMapping("/parlamentares")
    @Operation(summary = "Registers a parliamentarian (Deputy or Senator) (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parliamentarian created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ParliamentarianResponse> createParliamentarian(
            @Valid @RequestBody ParliamentarianRequest request) {
        ParliamentarianResponse response = transparencyService.createParliamentarian(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/parlamentares")
    @Operation(summary = "Lists parliamentarians with pagination (optional filter by chamber)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of parliamentarians"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ParliamentarianResponse>> listParliamentarians(
            @RequestParam(required = false) ParliamentarianChamber chamber,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(transparencyService.listParliamentarians(chamber, pageable));
    }

    @GetMapping("/parlamentares/{id}")
    @Operation(summary = "Finds a parliamentarian by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parliamentarian found"),
            @ApiResponse(responseCode = "404", description = "Parliamentarian not found")
    })
    public ResponseEntity<ParliamentarianResponse> getParliamentarian(@PathVariable Long id) {
        return ResponseEntity.ok(transparencyService.findParliamentarianById(id));
    }

    @DeleteMapping("/parlamentares/{id}")
    @Operation(summary = "Deletes (soft-delete) a parliamentarian by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Parliamentarian deleted"),
            @ApiResponse(responseCode = "404", description = "Parliamentarian not found")
    })
    public ResponseEntity<Void> deleteParliamentarian(@PathVariable Long id) {
        transparencyService.deleteParliamentarian(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/projetos-lei")
    @Operation(summary = "Registers a Legislative Bill (Projeto de Lei) (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Legislative bill created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Bill code already exists")
    })
    public ResponseEntity<LegislativeBillResponse> createBill(
            @Valid @RequestBody LegislativeBillRequest request) {
        LegislativeBillResponse response = transparencyService.createBill(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/projetos-lei")
    @Operation(summary = "Lists Legislative Bills with pagination (optional keyword search parameter q)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of legislative bills"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<LegislativeBillResponse>> listBills(
            @RequestParam(required = false) String q,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(transparencyService.listBills(q, pageable));
    }

    @GetMapping("/projetos-lei/{id}")
    @Operation(summary = "Finds a Legislative Bill by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Legislative bill found"),
            @ApiResponse(responseCode = "404", description = "Legislative bill not found")
    })
    public ResponseEntity<LegislativeBillResponse> getBill(@PathVariable Long id) {
        return ResponseEntity.ok(transparencyService.findBillById(id));
    }

    @DeleteMapping("/projetos-lei/{id}")
    @Operation(summary = "Deletes (soft-delete) a Legislative Bill by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Legislative bill deleted"),
            @ApiResponse(responseCode = "404", description = "Legislative bill not found")
    })
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        transparencyService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/despesas")
    @Operation(summary = "Records a parliamentary expense (CEAP) (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Expense recorded"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Parliamentarian not found")
    })
    public ResponseEntity<ParliamentarianExpenseResponse> createExpense(
            @Valid @RequestBody ParliamentarianExpenseRequest request) {
        ParliamentarianExpenseResponse response = transparencyService.createExpense(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/despesas")
    @Operation(summary = "Lists parliamentary expenses with pagination (optional filter by parliamentarianId)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of parliamentary expenses"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ParliamentarianExpenseResponse>> listExpenses(
            @RequestParam(name = "parlamentarId", required = false) Long parliamentarianId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(transparencyService.listExpenses(parliamentarianId, pageable));
    }

    @DeleteMapping("/despesas/{id}")
    @Operation(summary = "Deletes (soft-delete) a parliamentary expense by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expense deleted"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        transparencyService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
