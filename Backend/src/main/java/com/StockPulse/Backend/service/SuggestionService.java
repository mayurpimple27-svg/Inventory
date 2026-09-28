package com.StockPulse.Backend.service;

import com.StockPulse.Backend.commerce.CommerceAdvisorManager;
import com.StockPulse.Backend.commerce.CommerceRecommendation;
import com.StockPulse.Backend.commerce.ProductContext;
import com.StockPulse.Backend.commerce.enums.SuggestionStatus;
import com.StockPulse.Backend.commerce.enums.TriggerReason;
import com.StockPulse.Backend.model.PricingSuggestion;
import com.StockPulse.Backend.model.Product;
import com.StockPulse.Backend.model.ReorderSuggestion;
import com.StockPulse.Backend.repository.PricingSuggestionRepository;
import com.StockPulse.Backend.repository.ProductRepository;
import com.StockPulse.Backend.repository.ReorderSuggestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuggestionService {

    private final CommerceAdvisorManager advisorManager;
    private final PricingSuggestionRepository pricingSuggestionRepository;
    private final ReorderSuggestionRepository reorderSuggestionRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SuggestionService(
            CommerceAdvisorManager advisorManager,
            PricingSuggestionRepository pricingSuggestionRepository,
            ReorderSuggestionRepository reorderSuggestionRepository,
            ProductRepository productRepository) {

        this.advisorManager = advisorManager;
        this.pricingSuggestionRepository = pricingSuggestionRepository;
        this.reorderSuggestionRepository = reorderSuggestionRepository;
        this.productRepository = productRepository;
    }

    public PricingSuggestion generateAndSavePricingSuggestion(
            Product product,
            TriggerReason triggerReason) {

        // Prevent duplicate pending suggestions
        if (pricingSuggestionRepository
                .findPendingByProductAndTriggerReason(
                        product.getId(),
                        triggerReason,
                        SuggestionStatus.PENDING)
                .isPresent()) {

            return null;
        }

        double categoryAverageVelocity =
                calculateCategoryAverageVelocity(product.getCategory());

        ProductContext productContext =
                new ProductContext(
                        product,
                        product.getDemandVelocity());

        CommerceRecommendation recommendation =
                advisorManager
                        .getActiveAdvisor()
                        .recommend(
                                productContext,
                                categoryAverageVelocity,
                                triggerReason);

        PricingSuggestion pricingSuggestion =
                new PricingSuggestion(
                        product,
                        recommendation.getPricingRecommendation()
                                .getRecommendedPrice(),
                        recommendation.getPricingRecommendation()
                                .getDirection(),
                        recommendation.getPricingRecommendation()
                                .getConfidence(),
                        recommendation.getPricingRecommendation()
                                .getReasoning(),
                        triggerReason);

        return pricingSuggestionRepository.save(pricingSuggestion);
    }

    public ReorderSuggestion generateAndSaveReorderSuggestion(
            Product product,
            TriggerReason triggerReason) {

        // Prevent duplicate pending suggestions
        if (reorderSuggestionRepository
                .findPendingByProductAndTriggerReason(
                        product.getId(),
                        triggerReason,
                        SuggestionStatus.PENDING)
                .isPresent()) {

            return null;
        }

        double categoryAverageVelocity =
                calculateCategoryAverageVelocity(product.getCategory());

        ProductContext productContext =
                new ProductContext(
                        product,
                        product.getDemandVelocity());

        CommerceRecommendation recommendation =
                advisorManager
                        .getActiveAdvisor()
                        .recommend(
                                productContext,
                                categoryAverageVelocity,
                                triggerReason);

        ReorderSuggestion reorderSuggestion =
                new ReorderSuggestion(
                        product,
                        recommendation.getReorderRecommendation()
                                .getRecommendedQuantity(),
                        recommendation.getReorderRecommendation()
                                .getConfidence(),
                        recommendation.getReorderRecommendation()
                                .getReasoning(),
                        triggerReason);

        return reorderSuggestionRepository.save(reorderSuggestion);
    }

    private double calculateCategoryAverageVelocity(String category) {

        Double avg =
                productRepository
                        .findAverageDemandVelocityByCategory(category);

        return avg != null ? avg : 0.0;
    }

    public PricingSuggestion acceptPricingSuggestion(Long suggestionId) {

        PricingSuggestion suggestion =
                pricingSuggestionRepository
                        .findById(suggestionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pricing suggestion not found"));

        Product product = suggestion.getProduct();

        product.setPrice(
                suggestion.getRecommendedPrice());

        productRepository.save(product);

        suggestion.setStatus(
                SuggestionStatus.ACCEPTED);

        return pricingSuggestionRepository.save(suggestion);
    }

    public PricingSuggestion rejectPricingSuggestion(Long suggestionId) {

        PricingSuggestion suggestion =
                pricingSuggestionRepository
                        .findById(suggestionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pricing suggestion not found"));

        suggestion.setStatus(
                SuggestionStatus.REJECTED);

        return pricingSuggestionRepository.save(suggestion);
    }

    public ReorderSuggestion acceptReorderSuggestion(Long suggestionId) {

        ReorderSuggestion suggestion =
                reorderSuggestionRepository
                        .findById(suggestionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reorder suggestion not found"));

        Product product = suggestion.getProduct();

        product.setStock(
                product.getStock()
                        + suggestion.getRecommendedQuantity());

        productRepository.save(product);

        suggestion.setStatus(
                SuggestionStatus.ACCEPTED);

        return reorderSuggestionRepository.save(suggestion);
    }

    public ReorderSuggestion rejectReorderSuggestion(Long suggestionId) {

        ReorderSuggestion suggestion =
                reorderSuggestionRepository
                        .findById(suggestionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reorder suggestion not found"));

        suggestion.setStatus(
                SuggestionStatus.REJECTED);

        return reorderSuggestionRepository.save(suggestion);
    }
}