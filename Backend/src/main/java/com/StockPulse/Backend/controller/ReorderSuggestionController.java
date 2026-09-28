package com.StockPulse.Backend.controller;

import com.StockPulse.Backend.commerce.enums.SuggestionStatus;
import com.StockPulse.Backend.model.ReorderSuggestion;
import com.StockPulse.Backend.repository.ReorderSuggestionRepository;
import com.StockPulse.Backend.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reorder-suggestions")
@CrossOrigin(origins = "*")
public class ReorderSuggestionController {
    
    private final SuggestionService suggestionService;
    private final ReorderSuggestionRepository reorderSuggestionRepository;
    
    @Autowired
public ReorderSuggestionController(SuggestionService suggestionService, ReorderSuggestionRepository reorderSuggestionRepository) {
        this.suggestionService = suggestionService;
        this.reorderSuggestionRepository = reorderSuggestionRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<ReorderSuggestion>> getReorderSuggestions(
            @RequestParam(required = false) SuggestionStatus status) {
        if (status != null) {
            return ResponseEntity.ok(reorderSuggestionRepository.findByStatus(status));
        } else {
            return ResponseEntity.ok(reorderSuggestionRepository.findAll());
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<ReorderSuggestion> updateReorderSuggestionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String status = request.get("status");
        
        try {
            if ("ACCEPTED".equals(status)) {
                ReorderSuggestion accepted = suggestionService.acceptReorderSuggestion(id);
                return ResponseEntity.ok(accepted);
            } else if ("REJECTED".equals(status)) {
                ReorderSuggestion rejected = suggestionService.rejectReorderSuggestion(id);
                return ResponseEntity.ok(rejected);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}