package com.StockPulse.Backend.listener;

import com.StockPulse.Backend.event.OrderCreatedEvent;
import com.StockPulse.Backend.event.StockChangedEvent;
import com.StockPulse.Backend.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CommerceRecommendationListener {
    
    private final SuggestionService suggestionService;
    
    @Autowired
    public CommerceRecommendationListener(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }
    
    @Async
    @EventListener
    public void handleStockChangedEvent(StockChangedEvent event) {
        // Generate both pricing and reorder suggestions asynchronously
        suggestionService.generateAndSavePricingSuggestion(event.getProduct(), event.getTriggerReason());
        suggestionService.generateAndSaveReorderSuggestion(event.getProduct(), event.getTriggerReason());
    }
    
    @Async
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        // Generate both pricing and reorder suggestions asynchronously
        suggestionService.generateAndSavePricingSuggestion(event.getProduct(), event.getTriggerReason());
        suggestionService.generateAndSaveReorderSuggestion(event.getProduct(), event.getTriggerReason());
    }
}