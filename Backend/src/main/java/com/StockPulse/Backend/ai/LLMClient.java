package com.StockPulse.Backend.ai;

public interface LLMClient {
    String callLLM(String prompt);
}