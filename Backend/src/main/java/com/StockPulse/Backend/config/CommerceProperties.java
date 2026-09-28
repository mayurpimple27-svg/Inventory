package com.StockPulse.Backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "commerce")
public class CommerceProperties {
    
    private double demandSpikeMultiplier = 3.0;
    
    public double getDemandSpikeMultiplier() {
        return demandSpikeMultiplier;
    }
    
    public void setDemandSpikeMultiplier(double demandSpikeMultiplier) {
        this.demandSpikeMultiplier = demandSpikeMultiplier;
    }
}