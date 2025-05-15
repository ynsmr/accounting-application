package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CategoryRepository;
import com.cydeo.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.ref.PhantomReference;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;

    @Override
    public CategoryDto findById(Long categoryId) {
        return convertToDto(findCategoryById(categoryId));
    }
    
    private CategoryDto convertToDto(Category category){
        return mapperUtil.convert(category, new CategoryDto());
    }
    
    private Category findCategoryById(Long categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("No such category found"));
    }
    
}
