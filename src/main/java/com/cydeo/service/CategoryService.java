package com.cydeo.service;
import com.cydeo.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    
    CategoryDto findById(Long categoryId);
    List<CategoryDto> findAll();
    void saveCategory(CategoryDto categoryDto);
    void updateCategory(CategoryDto categoryDto);
    void deleteCategory(Long categoryId);
}
