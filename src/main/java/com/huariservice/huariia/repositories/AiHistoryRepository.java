package com.huariservice.huariia.repositories;

import com.huariservice.huariia.entities.AiHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link AiHistory} entries (AI assistant questions and answers).
 */
@Repository
public interface AiHistoryRepository extends JpaRepository<AiHistory, Long> {

    List<AiHistory> findByUserIdOrderByQueriedAtDesc(Long userId);

    Page<AiHistory> findByUserIdOrderByQueriedAtDesc(Long userId, Pageable pageable);
}
