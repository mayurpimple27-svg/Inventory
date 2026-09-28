package com.StockPulse.Backend.event;

import com.StockPulse.Backend.commerce.enums.TriggerReason;
import com.StockPulse.Backend.model.Product;

public class StockChangedEvent {
    private final Product product;
    private final TriggerReason triggerReason;
    
    public StockChangedEvent(Product product, TriggerReason triggerReason) {
        this.product = product;
        this.triggerReason = triggerReason;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public TriggerReason getTriggerReason() {
        return triggerReason;
    }
}