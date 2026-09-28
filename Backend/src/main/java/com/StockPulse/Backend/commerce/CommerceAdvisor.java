package com.StockPulse.Backend.commerce;

import com.StockPulse.Backend.commerce.enums.TriggerReason;

public interface CommerceAdvisor {
    
    CommerceRecommendation recommend(
        ProductContext productContext,
        double categoryAverageVelocity,
        TriggerReason triggerReason
    );
}