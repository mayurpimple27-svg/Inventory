package com.StockPulse.Backend.model;

import com.StockPulse.Backend.commerce.enums.SuggestionStatus;
import com.StockPulse.Backend.commerce.enums.TriggerReason;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reorder_suggestions")
public class ReorderSuggestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "current_stock")
    private int currentStock;
    
    @Column(name = "recommended_quantity")
    private int recommendedQuantity;
    
    @Column(name = "suggested_lead_time_days")
    private int suggestedLeadTimeDays;
    
    private double confidence;
    
    @Column(length = 1000)
    private String reasoning;
    
    @Enumerated(EnumType.STRING)
    private SuggestionStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_reason")
    private TriggerReason triggerReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public ReorderSuggestion() {
        this.createdAt = LocalDateTime.now();
        this.status = SuggestionStatus.PENDING;
        this.suggestedLeadTimeDays = 7; // default lead time
    }
    
    public ReorderSuggestion(Product product, int recommendedQuantity, double confidence, 
                           String reasoning, TriggerReason triggerReason) {
        this();
        this.product = product;
        this.currentStock = product.getStock();
        this.recommendedQuantity = recommendedQuantity;
        this.confidence = confidence;
        this.reasoning = reasoning;
        this.triggerReason = triggerReason;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public int getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }
    
    public int getRecommendedQuantity() {
        return recommendedQuantity;
    }
    
    public void setRecommendedQuantity(int recommendedQuantity) {
        this.recommendedQuantity = recommendedQuantity;
    }
    
    public int getSuggestedLeadTimeDays() {
        return suggestedLeadTimeDays;
    }
    
    public void setSuggestedLeadTimeDays(int suggestedLeadTimeDays) {
        this.suggestedLeadTimeDays = suggestedLeadTimeDays;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public String getReasoning() {
        return reasoning;
    }
    
    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
    
    public SuggestionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }
    
    public TriggerReason getTriggerReason() {
        return triggerReason;
    }
    
    public void setTriggerReason(TriggerReason triggerReason) {
        this.triggerReason = triggerReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}