package com.StockPulse.Backend.commerce;

public class ReorderRecommendation {
    private final int recommendedQuantity;
    private final double confidence;
    private final String reasoning;
    
    public ReorderRecommendation(int recommendedQuantity, double confidence, String reasoning) {
        this.recommendedQuantity = recommendedQuantity;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }
    
    public int getRecommendedQuantity() {
        return recommendedQuantity;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public String getReasoning() {
        return reasoning;
    }
}