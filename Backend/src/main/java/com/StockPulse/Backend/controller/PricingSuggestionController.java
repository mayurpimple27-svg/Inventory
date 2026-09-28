package com.StockPulse.Backend.controller;

import com.StockPulse.Backend.commerce.enums.SuggestionStatus;
import com.StockPulse.Backend.model.PricingSuggestion;
import com.StockPulse.Backend.repository.PricingSuggestionRepository;
import com.StockPulse.Backend.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing-suggestions")
@CrossOrigin(origins = "*")
public class PricingSuggestionController {
    
    private final SuggestionService suggestionService;
    private final PricingSuggestionRepository pricingSuggestionRepository;
    
    @Autowired
    public PricingSuggestionController(SuggestionService suggestionService, PricingSuggestionRepository pricingSuggestionRepository) {
        this.suggestionService = suggestionService;
        this.pricingSuggestionRepository = pricingSuggestionRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<PricingSuggestion>> getPricingSuggestions(
            @RequestParam(required = false) SuggestionStatus status) {
        if (status != null) {
            return ResponseEntity.ok(pricingSuggestionRepository.findByStatus(status));
        } else {
            return ResponseEntity.ok(pricingSuggestionRepository.findAll());
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<PricingSuggestion> updatePricingSuggestionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String status = request.get("status");
        
        try {
            if ("ACCEPTED".equals(status)) {
                PricingSuggestion accepted = suggestionService.acceptPricingSuggestion(id);
                return ResponseEntity.ok(accepted);
            } else if ("REJECTED".equals(status)) {
                PricingSuggestion rejected = suggestionService.rejectPricingSuggestion(id);
                return ResponseEntity.ok(rejected);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}