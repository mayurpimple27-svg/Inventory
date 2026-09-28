package com.StockPulse.Backend.repository;

import com.StockPulse.Backend.commerce.enums.SuggestionStatus;
import com.StockPulse.Backend.commerce.enums.TriggerReason;
import com.StockPulse.Backend.model.PricingSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingSuggestionRepository extends JpaRepository<PricingSuggestion, Long> {
    
    @Query("SELECT ps FROM PricingSuggestion ps WHERE ps.product.id = :productId AND ps.triggerReason = :triggerReason AND ps.status = :status")
    Optional<PricingSuggestion> findPendingByProductAndTriggerReason(
            @Param("productId") Long productId,
            @Param("triggerReason") TriggerReason triggerReason,
            @Param("status") SuggestionStatus status);
    
    List<PricingSuggestion> findByStatus(SuggestionStatus status);
}