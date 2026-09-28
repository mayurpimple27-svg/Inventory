package com.StockPulse.Backend.repository;

import com.StockPulse.Backend.commerce.enums.SuggestionStatus;
import com.StockPulse.Backend.commerce.enums.TriggerReason;
import com.StockPulse.Backend.model.ReorderSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, Long> {
    
    @Query("SELECT rs FROM ReorderSuggestion rs WHERE rs.product.id = :productId AND rs.triggerReason = :triggerReason AND rs.status = :status")
    Optional<ReorderSuggestion> findPendingByProductAndTriggerReason(
            @Param("productId") Long productId,
            @Param("triggerReason") TriggerReason triggerReason,
            @Param("status") SuggestionStatus status);
    
    List<ReorderSuggestion> findByStatus(SuggestionStatus status);
}