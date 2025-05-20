package com.cydeo.respository;

import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    boolean existsByCategory_Id(Long categoryId);
}
