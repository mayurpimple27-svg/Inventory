package com.StockPulse.Backend.commerce;

import com.StockPulse.Backend.model.Product;

public class ProductContext {
    private final Product product;
    private final double demandVelocity;
    
    public ProductContext(Product product, double demandVelocity) {
        this.product = product;
        this.demandVelocity = demandVelocity;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public double getDemandVelocity() {
        return demandVelocity;
    }
    
    public String getName() {
        return product.getName();
    }
    
    public String getCategory() {
        return product.getCategory();
    }
    
    public double getPrice() {
        return product.getPrice();
    }
    
    public int getStock() {
        return product.getStock();
    }
    
    public int getReorderLevel() {
        return product.getReorderLevel();
    }
}