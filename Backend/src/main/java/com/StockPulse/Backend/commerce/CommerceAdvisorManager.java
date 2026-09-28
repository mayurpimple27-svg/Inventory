package com.StockPulse.Backend.commerce;

import com.StockPulse.Backend.ai.AICommerceAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommerceAdvisorManager {
    
    private final Map<String, CommerceAdvisor> advisors;
    private String activeStrategy = "RULE";
    
    @Autowired
    public CommerceAdvisorManager(RuleBasedCommerceAdvisor ruleBasedAdvisor, AICommerceAdvisor aiAdvisor) {
        advisors = new HashMap<>();
        advisors.put("RULE", ruleBasedAdvisor);
        advisors.put("AI", aiAdvisor);
    }
    
    public CommerceAdvisor getActiveAdvisor() {
        return advisors.get(activeStrategy);
    }
    
    public void switchStrategy(String strategyName) {
        if (advisors.containsKey(strategyName)) {
            this.activeStrategy = strategyName;
        } else {
            throw new IllegalArgumentException("Unsupported strategy: " + strategyName);
        }
    }
    
    public String getActiveStrategy() {
        return activeStrategy;
    }
    
    public void registerAdvisor(String name, CommerceAdvisor advisor) {
        advisors.put(name, advisor);
    }
}