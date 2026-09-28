package com.StockPulse.Backend.event;

import com.StockPulse.Backend.commerce.enums.TriggerReason;
import com.StockPulse.Backend.model.Product;

public class OrderCreatedEvent {
    private final Product product;
    private final TriggerReason triggerReason;
    
    public OrderCreatedEvent(Product product, TriggerReason triggerReason) {
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