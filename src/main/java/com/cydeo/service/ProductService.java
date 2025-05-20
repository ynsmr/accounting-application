package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;

import java.util.List;

public interface ProductService {
    
    ProductDto findById(Long productId);
    List<ProductDto> listAllProducts();
    void deleteProduct(Long productId);
    void saveProduct(ProductDto productDto);
    void updateProduct(ProductDto productDto);
    boolean categoryHasProduct(Long categoryId);
}
