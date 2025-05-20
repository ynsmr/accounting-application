package com.cydeo.controller;

import com.cydeo.dto.ProductDto;
import com.cydeo.enums.ProductUnit;
import com.cydeo.service.CategoryService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public String listProductsPage(Model model){
        model.addAttribute("products", productService.listAllProducts());
        return "product/product-list";
    }
    
    @GetMapping("/create")
    public String createProductPage(Model model){
        model.addAttribute("newProduct", new ProductDto());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
        return "/product/product-create";
    }
    
    @GetMapping("/update/{productId}")
    public String updateProductPage(@PathVariable("productId") Long productId,  Model model){
        
        model.addAttribute("product", productService.findById(productId));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
        return "/product/product-update";
    }
    
    @GetMapping("/delete/{productId}")
    public String deleteProduct(@PathVariable("productId") Long productId){
        productService.deleteProduct(productId);
        return "redirect:/products/list";
    }
    
    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("newProduct") ProductDto productDto, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
            return "product/product-create";
        }
        productService.saveProduct(productDto);
        return "redirect:/products/list";
    }
    
    @PostMapping("/update/{productId}")
    public String updateProduct(@Valid @ModelAttribute("product") ProductDto productDto, BindingResult bindingResult, @PathVariable("productId") Long productId, Model model){
        productDto.setId(productId);
        if (bindingResult.hasErrors()){
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
            return "product/product-update";
        }
        productService.updateProduct(productDto);
        return "redirect:/products/list";
    }
}
