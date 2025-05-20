package com.cydeo.dto;

import com.cydeo.enums.ProductUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class ProductDto {
    
    private Long id;
    @NotBlank(message = "Name is a required field.")
    @Size(min = 2, max = 50, message = "Name should be 2-50 character long.")
    private String name;
    private int quantityInStock;
    @NotNull(message = "Low limit alert is a required field.")
    @Min(value = 1, message = "Low limit can not be less than 1.")
    private int lowLimitAlert;
    @NotNull(message = "Product unit is required field.")
    private ProductUnit productUnit;
    @NotNull(message = "Please select a product.")
    private CategoryDto category;
    private boolean hasProduct;
    
}
