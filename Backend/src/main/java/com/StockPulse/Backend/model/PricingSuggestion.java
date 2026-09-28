package com.StockPulse.Backend.model;

import java.time.LocalDateTime;

import com.StockPulse.Backend.commerce.enums.PricingDirection;
import com.StockPulse.Backend.commerce.enums.SuggestionStatus;
import com.StockPulse.Backend.commerce.enums.TriggerReason;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pricing_suggestions")
public class PricingSuggestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "current_price")
    private double currentPrice;
    
    @Column(name = "recommended_price")
    private double recommendedPrice;
    
    @Enumerated(EnumType.STRING)
    private PricingDirection direction;
    
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
    
    public PricingSuggestion() {
        this.createdAt = LocalDateTime.now();
        this.status = SuggestionStatus.PENDING;
    }
    
    public PricingSuggestion(Product product, double recommendedPrice, PricingDirection direction, 
                           double confidence, String reasoning, TriggerReason triggerReason) {
        this();
        this.product = product;
        this.currentPrice = product.getPrice();
        this.recommendedPrice = recommendedPrice;
        this.direction = direction;
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
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public double getRecommendedPrice() {
        return recommendedPrice;
    }
    
    public void setRecommendedPrice(double recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }
    
    public PricingDirection getDirection() {
        return direction;
    }
    
    public void setDirection(PricingDirection direction) {
        this.direction = direction;
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