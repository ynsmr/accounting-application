package com.cydeo.service;

import com.cydeo.dto.ProductDto;

import java.util.List;

public interface ProductService {
    
    ProductDto findById(Long productId);
    List<ProductDto> listAllProducts();
    void deleteProduct(Long productId);
    void saveProduct(ProductDto productDto);
    void updateProduct(ProductDto productDto);
}
