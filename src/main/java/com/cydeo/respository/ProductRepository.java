package com.cydeo.respository;


import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    boolean existsByCategory_Id(Long categoryId);

    List<Product> findProductsByCategory_Company_Id(Long categoryCompanyId);
    
}
