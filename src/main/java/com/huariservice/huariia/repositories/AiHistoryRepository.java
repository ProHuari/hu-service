package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.AiHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link AiHistory} entries (AI assistant questions and answers).
 */
@Repository
public interface AiHistoryRepository extends JpaRepository<AiHistory, Long> {

    /**
     * Lists a user's AI history, newest first.
     *
     * <p>Derived query: {@code WHERE user.id = ? ORDER BY queriedAt DESC}. Served efficiently by
     * the composite index {@code idx_historico_ia_usuario_data (usuario_id, data_consulta)}.</p>
     *
     * @param userId id of the user
     * @return history entries sorted from most recent to oldest (empty list if none)
     */
    List<AiHistory> findByUserIdOrderByQueriedAtDesc(Long userId);
}
