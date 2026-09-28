package com.StockPulse.Backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StockPulse.Backend.commerce.enums.TriggerReason;
import com.StockPulse.Backend.model.Product;
import com.StockPulse.Backend.service.ProductService;
import com.StockPulse.Backend.service.SuggestionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService service;
    private final SuggestionService suggestionService;

    public ProductController(ProductService service, SuggestionService suggestionService) {
        this.service = service;
        this.suggestionService = suggestionService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        try {
            Product product = service.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        return service.createProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product updatedProduct) {

        try {
            Product product = service.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            service.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts() {
        return service.getLowStockProducts();
    }

    @PostMapping("/{id}/suggest-pricing")
    public ResponseEntity<?> suggestPricing(@PathVariable Long id) {
        try {
            Product product = service.getProductById(id);
            var suggestion = suggestionService.generateAndSavePricingSuggestion(product, TriggerReason.MANUAL);
            if (suggestion == null) {
                return ResponseEntity.badRequest().body("Pending suggestion already exists");
            }
            return ResponseEntity.ok(suggestion);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/suggest-reorder")
    public ResponseEntity<?> suggestReorder(@PathVariable Long id) {
        try {
            Product product = service.getProductById(id);
            var suggestion = suggestionService.generateAndSaveReorderSuggestion(product, TriggerReason.MANUAL);
            if (suggestion == null) {
                return ResponseEntity.badRequest().body("Pending suggestion already exists");
            }
            return ResponseEntity.ok(suggestion);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/orders")
    public ResponseEntity<Product> simulateOrder(@PathVariable Long id) {
        try {
            Product updatedProduct = service.processOrder(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            int newStock = request.get("stock");
            Product updatedProduct = service.updateStock(id, newStock);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}