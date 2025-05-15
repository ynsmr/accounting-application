package com.cydeo.controller;

import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping("/list")
    public String listCategoryPage(Model model){
        
        return "category/category-list";
    }
    
    @GetMapping("/create")
    public String createCategoryPage(Model model){
        model.addAttribute("newCategory", new CategoryDto());
        
        return "category/category-create";
    }
    
    @GetMapping("/update/{categoryId}")
    public String updateCategory(@PathVariable("categoryId") Long categoryId{
        
        return "category/category-update";
    }
    
    @GetMapping("/delete/{categoryId}")
    public String deleteCategory(Long categoryId){
        
        return "redirect:/categories/list";
    }

    @PostMapping("/update/{categoryId}")
    public String updateCategory(Long categoryId, @ModelAttribute("category") CategoryDto categoryDto){
        categoryDto.setId(categoryId);
        
        return "redirect:/categories/list";
    }
    
    @PostMapping("/create")
    public String createCategory(@ModelAttribute CategoryDto categoryDto){

        return "redirect:/categories/list";
    }
    
    
}
