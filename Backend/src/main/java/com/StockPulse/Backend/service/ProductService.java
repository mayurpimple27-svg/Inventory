package com.StockPulse.Backend.service;

import com.StockPulse.Backend.commerce.enums.TriggerReason;
import com.StockPulse.Backend.config.CommerceProperties;
import com.StockPulse.Backend.event.OrderCreatedEvent;
import com.StockPulse.Backend.event.StockChangedEvent;
import com.StockPulse.Backend.exception.ProductNotFoundException;
import com.StockPulse.Backend.model.Product;
import com.StockPulse.Backend.repository.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommerceProperties commerceProperties;

    public ProductService(ProductRepository productRepository, ApplicationEventPublisher eventPublisher, CommerceProperties commerceProperties) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.commerceProperties = commerceProperties;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);
        
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setReorderLevel(updatedProduct.getReorderLevel());
        
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findByStockLessThanEqualReorderLevel();
    }

    public Product processOrder(Long id) {
        Product product = getProductById(id);
        
        // Decrement stock
        if (product.getStock() > 0) {
            product.setStock(product.getStock() - 1);
        }
        
        // Increment demand velocity
        product.incrementDemandVelocity();
        
        // Save updated product
        Product updatedProduct = productRepository.save(product);
        
        // Check triggers and publish events
        checkAndPublishEvents(updatedProduct);
        
        return updatedProduct;
    }
    
    public Product updateStock(Long id, int newStock) {
        Product product = getProductById(id);
        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        
        // Check triggers and publish events
        checkAndPublishEvents(updatedProduct);
        
        return updatedProduct;
    }
    
    private void checkAndPublishEvents(Product product) {
        // Check for inventory low condition
        if (product.getStock() < product.getReorderLevel()) {
            eventPublisher.publishEvent(new StockChangedEvent(product, TriggerReason.INVENTORY_LOW));
        }
        
        // Check for demand spike condition (using configurable multiplier)
        Double categoryAvg = productRepository.findAverageDemandVelocityByCategory(product.getCategory());
        double categoryAverage = categoryAvg != null ? categoryAvg : 0.0;
        
        if (product.getDemandVelocity() > commerceProperties.getDemandSpikeMultiplier() * categoryAverage && categoryAverage > 0) {
            eventPublisher.publishEvent(new OrderCreatedEvent(product, TriggerReason.DEMAND_SPIKE));
        }
    }
}