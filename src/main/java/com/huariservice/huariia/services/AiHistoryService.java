package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.AiHistoryResponse;
import com.huariservice.huariia.entities.AiHistory;
import com.huariservice.huariia.entities.User;
import com.huariservice.huariia.exceptions.ResourceNotFoundException;
import com.huariservice.huariia.repositories.AiHistoryRepository;
import com.huariservice.huariia.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Stores and reads the AI question/answer history.
 * The history is an append-only log: there is no update. {@code queriedAt} is filled by the entity.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AiHistoryService {

    private static final String HISTORY_NOT_FOUND = "Esse histórico não foi registrado";

    private final AiHistoryRepository historyRepository;
    private final UserRepository userRepository;

    /**
     * Records a question and its AI answer. The user id always comes from the token.
     *
     * @param userId   the authenticated user's id
     * @param question the question sent to the AI
     * @param answer   the answer produced by the AI
     * @return the stored history entry
     * @throws ResourceNotFoundException if the user does not exist
     */
    public AiHistoryResponse record(Long userId, String question, String answer) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Esse usuário não foi cadastrado"));

        AiHistory history = new AiHistory();
        history.setQuestion(question);
        history.setAnswer(answer);
        history.setUser(user);

        return AiHistoryResponse.from(historyRepository.save(history));
    }

    /**
     * Lists all history entries (admin view).
     *
     * @return every history entry
     */
    @Transactional(readOnly = true)
    public List<AiHistoryResponse> findAll() {
        return historyRepository.findAll().stream().map(AiHistoryResponse::from).toList();
    }

    /**
     * Lists the history of a user, most recent first.
     *
     * @param userId the user id
     * @return the user's history entries
     */
    @Transactional(readOnly = true)
    public List<AiHistoryResponse> findByUser(Long userId) {
        return historyRepository.findByUserIdOrderByQueriedAtDesc(userId).stream()
                .map(AiHistoryResponse::from).toList();
    }

    /**
     * Finds a history entry by id (students only access their own).
     *
     * @param id      the history entry id
     * @param userId  the authenticated user's id
     * @param isAdmin whether the authenticated user is an admin
     * @return the history entry
     * @throws ResourceNotFoundException if it does not exist or belongs to another user
     */
    @Transactional(readOnly = true)
    public AiHistoryResponse findById(Long id, Long userId, boolean isAdmin) {
        return AiHistoryResponse.from(getHistory(id, userId, isAdmin));
    }

    /**
     * Deletes a history entry (students only delete their own).
     *
     * @param id      the history entry id
     * @param userId  the authenticated user's id
     * @param isAdmin whether the authenticated user is an admin
     * @throws ResourceNotFoundException if it does not exist or belongs to another user
     */
    public void delete(Long id, Long userId, boolean isAdmin) {
        historyRepository.delete(getHistory(id, userId, isAdmin));
    }

    /**
     * Students only access their own history. Returns 404 (not 403) to avoid revealing that the id exists.
     */
    private AiHistory getHistory(Long id, Long userId, boolean isAdmin) {
        AiHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HISTORY_NOT_FOUND));

        if (!isAdmin && !history.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(HISTORY_NOT_FOUND);
        }
        return history;
    }
}
