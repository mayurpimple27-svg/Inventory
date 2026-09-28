package com.StockPulse.Backend.controller;

import com.StockPulse.Backend.commerce.CommerceAdvisorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/commerce")
@CrossOrigin(origins = "*")
public class CommerceController {
    
    private final CommerceAdvisorManager advisorManager;
    
    @Autowired
    public CommerceController(CommerceAdvisorManager advisorManager) {
        this.advisorManager = advisorManager;
    }
    
    @GetMapping("/strategy")
    public ResponseEntity<Map<String, String>> getStrategy() {
        Map<String, String> response = new HashMap<>();
        response.put("strategy", advisorManager.getActiveStrategy());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/strategy")
    public ResponseEntity<Map<String, String>> switchStrategy(@RequestBody Map<String, String> request) {
        String strategy = request.get("strategy");
        
        try {
            advisorManager.switchStrategy(strategy);
            Map<String, String> response = new HashMap<>();
            response.put("strategy", advisorManager.getActiveStrategy());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}