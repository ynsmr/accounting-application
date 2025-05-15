package com.cydeo.service;

import com.cydeo.dto.CategoryDto;

public interface CategoryService {
    
    CategoryDto findById(Long categoryId);
}
