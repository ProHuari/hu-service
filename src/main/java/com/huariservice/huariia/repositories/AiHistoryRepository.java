package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.AiHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for {@link AiHistory} entities.
 */
@Repository
public interface AiHistoryRepository extends JpaRepository<AiHistory, Long> {

    /**
     * Lists the AI history of a user, most recent first.
     *
     * @param userId the user id
     * @return the user's AI history entries ordered by query date descending
     */
    List<AiHistory> findByUserIdOrderByQueriedAtDesc(Long userId);
}
