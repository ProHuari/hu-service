package com.huariservice.huariia.services;

import com.huariservice.huariia.dto.ExpenseSummaryResponse;
import com.huariservice.huariia.dto.LegislativeBillRequest;
import com.huariservice.huariia.dto.LegislativeBillResponse;
import com.huariservice.huariia.dto.ParliamentarianExpenseRequest;
import com.huariservice.huariia.dto.ParliamentarianExpenseResponse;
import com.huariservice.huariia.dto.ParliamentarianRequest;
import com.huariservice.huariia.dto.ParliamentarianResponse;
import com.huariservice.huariia.entities.LegislativeBill;
import com.huariservice.huariia.entities.Parliamentarian;
import com.huariservice.huariia.entities.ParliamentarianExpense;
import com.huariservice.huariia.entities.enums.ParliamentarianChamber;
import com.huariservice.huariia.exceptions.ConflictException;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.LegislativeBillRepository;
import com.huariservice.huariia.repositories.ParliamentarianExpenseRepository;
import com.huariservice.huariia.repositories.ParliamentarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service managing public political data: Parliamentarians (Deputies and Senators),
 * Legislative Bills (Projetos de Lei), and Parliamentary Expenses (CEAP), as well as
 * building RAG context from Legislative Bills for the AI assistant.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PoliticalTransparencyService {

    private final ParliamentarianRepository parliamentarianRepository;
    private final LegislativeBillRepository legislativeBillRepository;
    private final ParliamentarianExpenseRepository expenseRepository;

    // ---------- Parliamentarians (Deputados & Senadores) ----------

    public ParliamentarianResponse createParliamentarian(ParliamentarianRequest request) {
        Parliamentarian entity = new Parliamentarian();
        entity.setName(request.name().trim());
        entity.setChamber(request.chamber());
        entity.setParty(request.party().trim().toUpperCase());
        entity.setState(request.state().trim().toUpperCase());
        entity.setExternalId(request.externalId());
        return ParliamentarianResponse.from(parliamentarianRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ParliamentarianResponse> listParliamentarians(ParliamentarianChamber chamber) {
        List<Parliamentarian> list = (chamber != null)
                ? parliamentarianRepository.findByChamber(chamber)
                : parliamentarianRepository.findAll();
        return list.stream().map(ParliamentarianResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Page<ParliamentarianResponse> listParliamentarians(ParliamentarianChamber chamber, Pageable pageable) {
        Page<Parliamentarian> page = (chamber != null)
                ? parliamentarianRepository.findByChamber(chamber, pageable)
                : parliamentarianRepository.findAll(pageable);
        return page.map(ParliamentarianResponse::from);
    }

    @Transactional(readOnly = true)
    public ParliamentarianResponse findParliamentarianById(Long id) {
        Parliamentarian entity = parliamentarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parliamentarian not found"));
        return ParliamentarianResponse.from(entity);
    }

    public ParliamentarianResponse updateParliamentarian(Long id, ParliamentarianRequest request) {
        Parliamentarian entity = parliamentarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parliamentarian not found"));
        entity.setName(request.name().trim());
        entity.setChamber(request.chamber());
        entity.setParty(request.party().trim().toUpperCase());
        entity.setState(request.state().trim().toUpperCase());
        entity.setExternalId(request.externalId());
        return ParliamentarianResponse.from(parliamentarianRepository.save(entity));
    }

    public void deleteParliamentarian(Long id) {
        Parliamentarian entity = parliamentarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parliamentarian not found"));
        parliamentarianRepository.delete(entity);
    }

    // ---------- Legislative Bills (Projetos de Lei - PLs) ----------

    public LegislativeBillResponse createBill(LegislativeBillRequest request) {
        if (legislativeBillRepository.existsByCodeIgnoreCase(request.code().trim())) {
            throw new ConflictException("A legislative bill with this code already exists");
        }
        LegislativeBill bill = new LegislativeBill();
        bill.setCode(request.code().trim());
        bill.setTitle(request.title().trim());
        bill.setSummary(request.summary().trim());
        bill.setFullText(request.fullText());
        bill.setStatus(request.status().trim());

        if (request.authorId() != null) {
            Parliamentarian author = parliamentarianRepository.findById(request.authorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author parliamentarian not found"));
            bill.setAuthor(author);
        }

        return LegislativeBillResponse.from(legislativeBillRepository.save(bill));
    }

    @Transactional(readOnly = true)
    public List<LegislativeBillResponse> listBills(String query) {
        List<LegislativeBill> bills = (query != null && !query.isBlank())
                ? legislativeBillRepository
                        .findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrCodeContainingIgnoreCase(
                                query.trim(), query.trim(), query.trim())
                : legislativeBillRepository.findAll();
        return bills.stream().map(LegislativeBillResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Page<LegislativeBillResponse> listBills(String query, Pageable pageable) {
        Page<LegislativeBill> bills = (query != null && !query.isBlank())
                ? legislativeBillRepository
                        .findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrCodeContainingIgnoreCase(
                                query.trim(), query.trim(), query.trim(), pageable)
                : legislativeBillRepository.findAll(pageable);
        return bills.map(LegislativeBillResponse::from);
    }

    @Transactional(readOnly = true)
    public LegislativeBillResponse findBillById(Long id) {
        LegislativeBill bill = legislativeBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legislative bill not found"));
        return LegislativeBillResponse.from(bill);
    }

    public LegislativeBillResponse updateBill(Long id, LegislativeBillRequest request) {
        LegislativeBill bill = legislativeBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legislative bill not found"));

        String trimmedCode = request.code().trim();
        if (!bill.getCode().equalsIgnoreCase(trimmedCode)
                && legislativeBillRepository.existsByCodeIgnoreCase(trimmedCode)) {
            throw new ConflictException("A legislative bill with this code already exists");
        }

        bill.setCode(trimmedCode);
        bill.setTitle(request.title().trim());
        bill.setSummary(request.summary().trim());
        bill.setFullText(request.fullText());
        bill.setStatus(request.status().trim());

        if (request.authorId() != null) {
            Parliamentarian author = parliamentarianRepository.findById(request.authorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author parliamentarian not found"));
            bill.setAuthor(author);
        } else {
            bill.setAuthor(null);
        }

        return LegislativeBillResponse.from(legislativeBillRepository.save(bill));
    }

    public void deleteBill(Long id) {
        LegislativeBill bill = legislativeBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legislative bill not found"));
        legislativeBillRepository.delete(bill);
    }

    // ---------- Parliamentary Expenses (Despesas / CEAP) ----------

    public ParliamentarianExpenseResponse createExpense(ParliamentarianExpenseRequest request) {
        Parliamentarian parliamentarian = parliamentarianRepository.findById(request.parliamentarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Parliamentarian not found"));

        ParliamentarianExpense expense = new ParliamentarianExpense();
        expense.setParliamentarian(parliamentarian);
        expense.setExpenseCategory(request.expenseCategory().trim());
        expense.setSupplierName(request.supplierName().trim());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setDocumentUrl(request.documentUrl());

        return ParliamentarianExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public List<ParliamentarianExpenseResponse> listExpenses(Long parliamentarianId) {
        List<ParliamentarianExpense> expenses = (parliamentarianId != null)
                ? expenseRepository.findByParliamentarianIdOrderByExpenseDateDesc(parliamentarianId)
                : expenseRepository.findAll();
        return expenses.stream().map(ParliamentarianExpenseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Page<ParliamentarianExpenseResponse> listExpenses(Long parliamentarianId, Pageable pageable) {
        Page<ParliamentarianExpense> expenses = (parliamentarianId != null)
                ? expenseRepository.findByParliamentarianIdOrderByExpenseDateDesc(parliamentarianId, pageable)
                : expenseRepository.findAll(pageable);
        return expenses.map(ParliamentarianExpenseResponse::from);
    }

    @Transactional(readOnly = true)
    public ParliamentarianExpenseResponse findExpenseById(Long id) {
        ParliamentarianExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        return ParliamentarianExpenseResponse.from(expense);
    }

    @Transactional(readOnly = true)
    public ExpenseSummaryResponse getExpenseSummary() {
        return new ExpenseSummaryResponse(
                expenseRepository.sumTotalAmount(),
                expenseRepository.count(),
                expenseRepository.findRankingByParliamentarian(),
                expenseRepository.findDistributionByCategory(),
                expenseRepository.findDistributionByState()
        );
    }

    public ParliamentarianExpenseResponse updateExpense(Long id, ParliamentarianExpenseRequest request) {
        ParliamentarianExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        Parliamentarian parliamentarian = parliamentarianRepository.findById(request.parliamentarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Parliamentarian not found"));

        expense.setParliamentarian(parliamentarian);
        expense.setExpenseCategory(request.expenseCategory().trim());
        expense.setSupplierName(request.supplierName().trim());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setDocumentUrl(request.documentUrl());

        return ParliamentarianExpenseResponse.from(expenseRepository.save(expense));
    }

    public void deleteExpense(Long id) {
        ParliamentarianExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        expenseRepository.delete(expense);
    }

    // ---------- RAG Context Retrieval ----------

    private static final Set<String> RAG_STOP_WORDS = Set.of(
            "para", "como", "sobre", "qual", "quais", "isso", "esse", "essa", "esta", "este",
            "pelo", "pela", "entre", "mais", "muito", "pode", "fazer", "tudo", "quem", "onde",
            "quando", "porque", "projeto", "projetos"
    );

    @Transactional(readOnly = true)
    public String buildRagContextForQuestion(String question) {
        if (question == null || question.isBlank()) {
            return "";
        }

        List<String> keywords = extractKeywords(question);
        Map<Long, LegislativeBill> matchedById = new LinkedHashMap<>();

        for (String keyword : keywords) {
            if (matchedById.size() >= 3) {
                break;
            }
            Page<LegislativeBill> page = legislativeBillRepository
                    .findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrCodeContainingIgnoreCase(
                            keyword, keyword, keyword, PageRequest.of(0, 3));
            for (LegislativeBill bill : page.getContent()) {
                matchedById.putIfAbsent(bill.getId(), bill);
                if (matchedById.size() >= 3) {
                    break;
                }
            }
        }

        List<LegislativeBill> selected = matchedById.isEmpty()
                ? legislativeBillRepository.findAll(PageRequest.of(0, 2)).getContent()
                : List.copyOf(matchedById.values());

        if (selected.isEmpty()) {
            return "";
        }

        return selected.stream()
                .map(b -> String.format(
                        "[Projeto de Lei: %s | Título: %s | Status: %s | Ementa: %s | Texto: %s]",
                        b.getCode(),
                        b.getTitle(),
                        b.getStatus(),
                        b.getSummary(),
                        b.getFullText() != null ? b.getFullText() : "N/A"))
                .collect(Collectors.joining("\n"));
    }

    private List<String> extractKeywords(String question) {
        return Arrays.stream(question.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}/-]+"))
                .map(String::trim)
                .filter(token -> token.length() >= 4 && !RAG_STOP_WORDS.contains(token))
                .distinct()
                .limit(5)
                .toList();
    }
}

