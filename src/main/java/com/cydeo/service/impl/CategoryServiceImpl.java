package com.cydeo.service.impl;


import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.exception.CategoryNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CompanyService companyService;
    private final ProductService productService;
    private final MapperUtil mapperUtil;

    @Override
    public CategoryDto findById(Long categoryId) {
        CategoryDto categoryDto = convertToDto(findCategoryById(categoryId));
        categoryDto.setHasProduct(productService.categoryHasProduct(categoryId));
        return categoryDto;
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getCompany() != null && category.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .map(this::convertToDto)
                .peek(categoryDto -> categoryDto.setHasProduct(productService.categoryHasProduct(categoryDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void saveCategory(CategoryDto categoryDto) {
        categoryDto.setCompany(companyService.findById(companyService.retrieveCurrentCompany()));
        categoryRepository.save(convertToEntity(categoryDto));

    }

    @Override
    public void updateCategory(CategoryDto categoryDto) {
        Category category = findCategoryById(categoryDto.getId());
        category.setDescription(categoryDto.getDescription());
        categoryRepository.save(category);
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
                .orElseThrow(() -> new CategoryNotFoundException("No such category found"));
    }
    
 
    
}