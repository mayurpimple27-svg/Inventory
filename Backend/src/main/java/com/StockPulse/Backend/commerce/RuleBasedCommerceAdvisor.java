package com.StockPulse.Backend.commerce;

import com.StockPulse.Backend.commerce.enums.PricingDirection;
import com.StockPulse.Backend.commerce.enums.TriggerReason;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedCommerceAdvisor implements CommerceAdvisor {
    
    @Override
    public CommerceRecommendation recommend(
            ProductContext productContext,
            double categoryAverageVelocity,
            TriggerReason triggerReason) {
        
        // Pricing recommendation
        PricingRecommendation pricingRecommendation = generatePricingRecommendation(
                productContext, categoryAverageVelocity, triggerReason);
        
        // Reorder recommendation
        ReorderRecommendation reorderRecommendation = generateReorderRecommendation(
                productContext, triggerReason);
        
        return new CommerceRecommendation(pricingRecommendation, reorderRecommendation);
    }
    
    private PricingRecommendation generatePricingRecommendation(
            ProductContext productContext,
            double categoryAverageVelocity,
            TriggerReason triggerReason) {
        
        double currentPrice = productContext.getPrice();
        int stock = productContext.getStock();
        int reorderLevel = productContext.getReorderLevel();
        double demandVelocity = productContext.getDemandVelocity();
        
        double recommendedPrice;
        PricingDirection direction;
        String reasoning;
        
        if (stock < reorderLevel) {
            recommendedPrice = currentPrice * 1.10;
            direction = PricingDirection.INCREASE;
            reasoning = "Stock is below the reorder threshold, so the baseline strategy recommends a 10% price increase to protect remaining inventory.";
        } else if (demandVelocity > 2 * categoryAverageVelocity) {
            recommendedPrice = currentPrice * 1.05;
            direction = PricingDirection.INCREASE;
            reasoning = "Demand velocity is more than 2x the category average, so the baseline strategy recommends a 5% price increase.";
        } else {
            recommendedPrice = currentPrice;
            direction = PricingDirection.HOLD;
            reasoning = "Neither low-stock nor demand-spike conditions were met, so the strategy recommends HOLD.";
        }
        
        return new PricingRecommendation(recommendedPrice, direction, 1.0, reasoning);
    }
    
    private ReorderRecommendation generateReorderRecommendation(
            ProductContext productContext,
            TriggerReason triggerReason) {
        
        int reorderLevel = productContext.getReorderLevel();
        int currentStock = productContext.getStock();
        
        // quantity = (reorderLevel × 3) - currentStock
        int quantity = (reorderLevel * 3) - currentStock;
        
        // if quantity < 1, quantity = 1
        if (quantity < 1) {
            quantity = 1;
        }
        
        String reasoning = String.format(
                "Calculated using formula: (reorderLevel × 3) - currentStock = (%d × 3) - %d = %d. Adjusted to minimum 1 if below threshold.",
                reorderLevel, currentStock, quantity);
        
        return new ReorderRecommendation(quantity, 1.0, reasoning);
    }
}