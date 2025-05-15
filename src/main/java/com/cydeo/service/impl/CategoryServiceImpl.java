package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CategoryRepository;
import com.cydeo.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.ref.PhantomReference;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;

    @Override
    public CategoryDto findById(Long categoryId) {
        return convertToDto(findCategoryById(categoryId));
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveCategory(CategoryDto categoryDto) {
        categoryRepository.save(convertToEntity(categoryDto));

    }

    @Override
    public void updateCategory(CategoryDto categoryDto) {
        categoryRepository.save(convertToEntity(categoryDto));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        softDeleteCategory(findCategoryById(categoryId));

    }

    private CategoryDto convertToDto(Category category){
        return mapperUtil.convert(category, new CategoryDto());
    }
    
    private Category convertToEntity(CategoryDto categoryDto){
        return mapperUtil.convert(categoryDto, new Category());
    }
    
    private void softDeleteCategory(Category category){
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }
    
    private Category findCategoryById(Long categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("No such category found"));
    }
    
}
