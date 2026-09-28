package com.StockPulse.Backend.repository;

import com.StockPulse.Backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p WHERE p.stock <= p.reorderLevel")
    List<Product> findByStockLessThanEqualReorderLevel();
    
    @Query("SELECT AVG(p.demandVelocity) FROM Product p WHERE p.category = :category")
    Double findAverageDemandVelocityByCategory(String category);
}