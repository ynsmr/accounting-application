package com.cydeo.controller;
import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping("/list")
    public String listCategoryPage(Model model){
        model.addAttribute("categories", categoryService.findAll());
        return "category/category-list";
    }
    
    @GetMapping("/create")
    public String createCategoryPage(Model model){
        model.addAttribute("newCategory", new CategoryDto());
        return "category/category-create";
    }
    
    @GetMapping("/update/{categoryId}")
    public String updateCategory(@PathVariable("categoryId") Long categoryId, Model model){
        model.addAttribute("category", categoryService.findById(categoryId));
        return "category/category-update";
    }
    
    @GetMapping("/delete/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Long categoryId){
        categoryService.deleteCategory(categoryId);
        return "redirect:/categories/list";
    }

    @PostMapping("/update/{categoryId}")
    public String updateCategory(@Valid @ModelAttribute("category") CategoryDto categoryDto,BindingResult bindingResult, @PathVariable("categoryId") Long categoryId ){
        categoryDto.setId(categoryId);
        if (bindingResult.hasErrors()){
            return "category/category-update";
        }
        
        categoryService.updateCategory(categoryDto);
        return "redirect:/categories/list";
    }
    
    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute("newCategory") CategoryDto categoryDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "category/category-create";
        }
        
        categoryService.saveCategory(categoryDto);
        return "redirect:/categories/list";
    }
    
    
}
