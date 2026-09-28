package com.StockPulse.Backend.commerce;

import com.StockPulse.Backend.commerce.enums.PricingDirection;

public class PricingRecommendation {
    private final double recommendedPrice;
    private final PricingDirection direction;
    private final double confidence;
    private final String reasoning;
    
    public PricingRecommendation(double recommendedPrice, PricingDirection direction, double confidence, String reasoning) {
        this.recommendedPrice = recommendedPrice;
        this.direction = direction;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }
    
    public double getRecommendedPrice() {
        return recommendedPrice;
    }
    
    public PricingDirection getDirection() {
        return direction;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public String getReasoning() {
        return reasoning;
    }
}