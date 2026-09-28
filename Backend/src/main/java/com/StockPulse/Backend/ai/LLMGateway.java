package com.StockPulse.Backend.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LLMGateway implements LLMClient {
    
    @Value("${llm.provider:mock}")
    private String provider;
    
    @Value("${llm.model:default}")
    private String model;
    
    @Value("${llm.api-key:}")
    private String apiKey;
    
    @Value("${llm.base-url:}")
    private String baseUrl;
    
    @Override
    public String callLLM(String prompt) {
        // Mock implementation for demonstration
        // In a real implementation, this would call the actual LLM API
        System.out.println("Calling LLM with provider: " + provider + ", model: " + model);
        System.out.println("Prompt: " + prompt);
        
        // Return a mock response for demonstration
        return "{\n" +
               "  \"pricing\": {\n" +
               "    \"recommendedPrice\": 29.99,\n" +
               "    \"direction\": \"INCREASE\",\n" +
               "    \"confidence\": 0.85,\n" +
               "    \"reasoning\": \"Mock AI response for demonstration purposes\"\n" +
               "  },\n" +
               "  \"reorder\": {\n" +
               "    \"recommendedQuantity\": 100,\n" +
               "    \"confidence\": 0.80,\n" +
               "    \"reasoning\": \"Mock AI response for demonstration purposes\"\n" +
               "  }\n" +
               "}";
    }
}