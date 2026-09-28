package com.StockPulse.Backend.commerce;

public class CommerceRecommendation {
    private final PricingRecommendation pricingRecommendation;
    private final ReorderRecommendation reorderRecommendation;
    
    public CommerceRecommendation(PricingRecommendation pricingRecommendation, ReorderRecommendation reorderRecommendation) {
        this.pricingRecommendation = pricingRecommendation;
        this.reorderRecommendation = reorderRecommendation;
    }
    
    public PricingRecommendation getPricingRecommendation() {
        return pricingRecommendation;
    }
    
    public ReorderRecommendation getReorderRecommendation() {
        return reorderRecommendation;
    }
}