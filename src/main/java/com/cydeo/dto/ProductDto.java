package com.cydeo.dto;

import com.cydeo.enums.ProductUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProductDto {
    
    private Long id;
    private String name;
    private int quantityInStock;
    private int lowLimitAlert;
    private ProductUnit productUnit;
    private CategoryDto categoryDto;
    private boolean hasProduct;
}
