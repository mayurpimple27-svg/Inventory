package com.StockPulse.Backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.StockPulse.Backend.ai.LLMGateway;
import com.StockPulse.Backend.commerce.CommerceAdvisor;
import com.StockPulse.Backend.commerce.CommerceRecommendation;
import com.StockPulse.Backend.commerce.PricingRecommendation;
import com.StockPulse.Backend.commerce.ReorderRecommendation;
import com.StockPulse.Backend.commerce.ProductContext;
import com.StockPulse.Backend.commerce.enums.PricingDirection;
import com.StockPulse.Backend.commerce.enums.TriggerReason;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AICommerceAdvisor implements CommerceAdvisor {

    private final LLMGateway llmGateway;
    private final ObjectMapper objectMapper;

    @Value("${commerce.demand-spike.multiplier:3.0}")
    private double velocityMultiplier;

    public AICommerceAdvisor(LLMGateway llmGateway) {
        this.llmGateway = llmGateway;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CommerceRecommendation recommend(
            ProductContext productContext,
            double categoryAverageVelocity,
            TriggerReason triggerReason) {

        String prompt;

        switch (triggerReason) {
            case INVENTORY_LOW:
                prompt = buildInventoryLowPrompt(
                        productContext,
                        categoryAverageVelocity
                );
                break;

            case DEMAND_SPIKE:
                prompt = buildDemandSpikePrompt(
                        productContext,
                        categoryAverageVelocity
                );
                break;

            case MANUAL:
            case INITIAL:
            default:
                prompt = buildManualPrompt(
                        productContext,
                        categoryAverageVelocity
                );
                break;
        }

        try {
            String jsonResponse = llmGateway.callLLM(prompt);

            return parseAIResponse(
                    jsonResponse,
                    productContext
            );

        } catch (Exception e) {
            return createFallbackRecommendation(
                    productContext,
                    categoryAverageVelocity
            );
        }
    }

    private String buildInventoryLowPrompt(
            ProductContext productContext,
            double categoryAverageVelocity) {

        return """
                You are an AI commerce advisor.

                The product has triggered an INVENTORY_LOW event.

                Product Details:
                - Name: %s
                - Category: %s
                - Current Price: %.2f
                - Current Stock: %d
                - Reorder Threshold: %d
                - Current Demand Velocity: %.2f
                - Category Average Demand Velocity: %.2f

                This is an inventory-low situation.

                Consider the merchandising tradeoff:
                1. Protect remaining inventory with a modest price increase.
                2. Consider whether demand is strong enough to justify an increase.
                3. Replenishment should account for the current stock and reorder threshold.

                Return ONLY valid JSON.

                Required format:
                {
                  "pricing": {
                    "recommendedPrice": 29.99,
                    "direction": "INCREASE",
                    "confidence": 0.82,
                    "reasoning": "..."
                  },
                  "reorder": {
                    "recommendedQuantity": 150,
                    "confidence": 0.78,
                    "reasoning": "..."
                  }
                }
                """.formatted(
                productContext.getName(),
                productContext.getCategory(),
                productContext.getPrice(),
                productContext.getStock(),
                productContext.getReorderLevel(),
                productContext.getDemandVelocity(),
                categoryAverageVelocity
        );
    }

    private String buildDemandSpikePrompt(
            ProductContext productContext,
            double categoryAverageVelocity) {

        double multiplier = categoryAverageVelocity == 0
                ? 0
                : productContext.getDemandVelocity() / categoryAverageVelocity;

        return """
                You are an AI commerce advisor.

                The product has triggered a DEMAND_SPIKE event.

                Product Details:
                - Name: %s
                - Category: %s
                - Current Price: %.2f
                - Current Stock: %d
                - Reorder Threshold: %d
                - Current Demand Velocity: %.2f
                - Category Average Demand Velocity: %.2f
                - Current Velocity Multiplier: %.2fx
                - Configured Spike Threshold: %.2fx

                Demand is significantly above the category average.

                Evaluate whether a modest price increase is appropriate while
                considering the need to replenish inventory.

                Do not make an extreme price increase.

                Return ONLY valid JSON.

                Required format:
                {
                  "pricing": {
                    "recommendedPrice": 29.99,
                    "direction": "INCREASE",
                    "confidence": 0.82,
                    "reasoning": "..."
                  },
                  "reorder": {
                    "recommendedQuantity": 150,
                    "confidence": 0.78,
                    "reasoning": "..."
                  }
                }
                """.formatted(
                productContext.getName(),
                productContext.getCategory(),
                productContext.getPrice(),
                productContext.getStock(),
                productContext.getReorderLevel(),
                productContext.getDemandVelocity(),
                categoryAverageVelocity,
                multiplier,
                velocityMultiplier
        );
    }

    private String buildManualPrompt(
            ProductContext productContext,
            double categoryAverageVelocity) {

        return """
                You are an AI commerce advisor.

                A MANUAL pricing and replenishment analysis is requested.

                Product Details:
                - Name: %s
                - Category: %s
                - Current Price: %.2f
                - Current Stock: %d
                - Reorder Threshold: %d
                - Current Demand Velocity: %.2f
                - Category Average Demand Velocity: %.2f

                Recommend an appropriate price and reorder quantity.

                Return ONLY valid JSON.

                Required format:
                {
                  "pricing": {
                    "recommendedPrice": 29.99,
                    "direction": "INCREASE",
                    "confidence": 0.82,
                    "reasoning": "..."
                  },
                  "reorder": {
                    "recommendedQuantity": 150,
                    "confidence": 0.78,
                    "reasoning": "..."
                  }
                }
                """.formatted(
                productContext.getName(),
                productContext.getCategory(),
                productContext.getPrice(),
                productContext.getStock(),
                productContext.getReorderLevel(),
                productContext.getDemandVelocity(),
                categoryAverageVelocity
        );
    }

    private CommerceRecommendation parseAIResponse(
            String jsonResponse,
            ProductContext productContext) throws Exception {

        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        JsonNode pricingNode = rootNode.get("pricing");
        JsonNode reorderNode = rootNode.get("reorder");

        if (pricingNode == null || reorderNode == null) {
            throw new IllegalArgumentException(
                    "Missing pricing or reorder object"
            );
        }

        if (!pricingNode.has("recommendedPrice")
                || !pricingNode.has("direction")
                || !pricingNode.has("confidence")
                || !pricingNode.has("reasoning")) {

            throw new IllegalArgumentException(
                    "Incomplete pricing recommendation"
            );
        }

        if (!reorderNode.has("recommendedQuantity")
                || !reorderNode.has("confidence")
                || !reorderNode.has("reasoning")) {

            throw new IllegalArgumentException(
                    "Incomplete reorder recommendation"
            );
        }

        double recommendedPrice =
                pricingNode.get("recommendedPrice").asDouble();

        String directionStr =
                pricingNode.get("direction").asText();

        PricingDirection direction =
                PricingDirection.valueOf(directionStr);

        double pricingConfidence =
                pricingNode.get("confidence").asDouble();

        String pricingReasoning =
                pricingNode.get("reasoning").asText();

        int recommendedQuantity =
                reorderNode.get("recommendedQuantity").asInt();

        double reorderConfidence =
                reorderNode.get("confidence").asDouble();

        String reorderReasoning =
                reorderNode.get("reasoning").asText();

        PricingRecommendation pricingRecommendation =
                new PricingRecommendation(
                        recommendedPrice,
                        direction,
                        pricingConfidence,
                        pricingReasoning
                );

        ReorderRecommendation reorderRecommendation =
                new ReorderRecommendation(
                        recommendedQuantity,
                        reorderConfidence,
                        reorderReasoning
                );

        if (!isValidPricingRecommendation(
                pricingRecommendation,
                productContext.getPrice())) {

            throw new IllegalArgumentException(
                    "Invalid AI pricing recommendation"
            );
        }

        if (!isValidReorderRecommendation(
                reorderRecommendation)) {

            throw new IllegalArgumentException(
                    "Invalid AI reorder recommendation"
            );
        }

        return new CommerceRecommendation(
                pricingRecommendation,
                reorderRecommendation
        );
    }

    private boolean isValidPricingRecommendation(
            PricingRecommendation recommendation,
            double currentPrice) {

        double recommendedPrice =
                recommendation.getRecommendedPrice();

        double confidence =
                recommendation.getConfidence();

        return recommendedPrice > 0
                && recommendedPrice <= currentPrice * 10
                && confidence >= 0.0
                && confidence <= 1.0;
    }

    private boolean isValidReorderRecommendation(
            ReorderRecommendation recommendation) {

        return recommendation.getRecommendedQuantity() >= 1
                && recommendation.getConfidence() >= 0.0
                && recommendation.getConfidence() <= 1.0;
    }

    private CommerceRecommendation createFallbackRecommendation(
            ProductContext productContext,
            double categoryAverageVelocity) {

        double currentPrice =
                productContext.getPrice();

        int stock =
                productContext.getStock();

        int reorderLevel =
                productContext.getReorderLevel();

        double demandVelocity =
                productContext.getDemandVelocity();

        double recommendedPrice;
        PricingDirection direction;
        String pricingReasoning;

        if (stock < reorderLevel) {

            recommendedPrice =
                    currentPrice * 1.10;

            direction =
                    PricingDirection.INCREASE;

            pricingReasoning =
                    "AI failed, falling back to rule-based strategy. "
                    + "Stock is below reorder threshold, so a 10% "
                    + "price increase is recommended.";

        } else if (demandVelocity > 2 * categoryAverageVelocity) {

            recommendedPrice =
                    currentPrice * 1.05;

            direction =
                    PricingDirection.INCREASE;

            pricingReasoning =
                    "AI failed, falling back to rule-based strategy. "
                    + "Demand velocity is more than 2x the category "
                    + "average, so a 5% price increase is recommended.";

        } else {

            recommendedPrice =
                    currentPrice;

            direction =
                    PricingDirection.HOLD;

            pricingReasoning =
                    "AI failed, falling back to rule-based strategy. "
                    + "No pricing trigger is active, so HOLD is recommended.";
        }

        PricingRecommendation pricingRecommendation =
                new PricingRecommendation(
                        recommendedPrice,
                        direction,
                        1.0,
                        pricingReasoning
                );

        int quantity =
                (reorderLevel * 3) - stock;

        if (quantity < 1) {
            quantity = 1;
        }

        String reorderReasoning =
                "AI failed, falling back to rule-based strategy. "
                + "Reorder quantity calculated using "
                + "(reorderLevel × 3) - currentStock.";

        ReorderRecommendation reorderRecommendation =
                new ReorderRecommendation(
                        quantity,
                        1.0,
                        reorderReasoning
                );

        return new CommerceRecommendation(
                pricingRecommendation,
                reorderRecommendation
        );
    }
}