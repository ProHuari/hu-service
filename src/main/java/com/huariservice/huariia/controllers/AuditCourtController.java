package com.huariservice.huariia.controllers;

import com.huariservice.huariia.dto.auditcourt.AuditCourtIrregularAccountResponse;
import com.huariservice.huariia.dto.auditcourt.AuditCourtProcessResponse;
import com.huariservice.huariia.dto.auditcourt.AuditCourtSummaryResponse;
import com.huariservice.huariia.services.AuditCourtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transparencia/tribunal-contas")
@Tag(name = "Audit Court Integration (TCU / TCE)", description = "Public oversight and audit records from the Court of Auditors")
@RequiredArgsConstructor
public class AuditCourtController {

    private final AuditCourtService auditCourtService;

    @GetMapping("/resumo")
    @Operation(summary = "Returns consolidated audit records and service status from Tribunal de Contas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consolidated audit court data retrieved successfully")
    })
    public ResponseEntity<AuditCourtSummaryResponse> getSummary(
            @RequestParam(name = "q", required = false) String queryParam,
            @RequestParam(name = "termo", required = false) String termoParam) {
        String query = queryParam != null ? queryParam : termoParam;
        return ResponseEntity.ok(auditCourtService.fetchTransparencyOverview(query));
    }

    @GetMapping("/processos")
    @Operation(summary = "Lists audit and oversight processes from Tribunal de Contas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processes retrieved successfully")
    })
    public ResponseEntity<List<AuditCourtProcessResponse>> getProcesses(
            @RequestParam(name = "subject", required = false) String subjectParam,
            @RequestParam(name = "assunto", required = false) String assuntoParam,
            @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        String subject = subjectParam != null ? subjectParam : assuntoParam;
        return ResponseEntity.ok(auditCourtService.fetchProcesses(subject, limit));
    }

    @GetMapping("/contas-irregulares")
    @Operation(summary = "Lists irregular public accounts judgments from Tribunal de Contas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Irregular accounts retrieved successfully")
    })
    public ResponseEntity<List<AuditCourtIrregularAccountResponse>> getIrregularAccounts(
            @RequestParam(name = "name", required = false) String nameParam,
            @RequestParam(name = "nome", required = false) String nomeParam) {
        String name = nameParam != null ? nameParam : nomeParam;
        return ResponseEntity.ok(auditCourtService.fetchIrregularAccounts(name));
    }
}

