package com.cydeo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceProductDto {
    
    private Long id;
    @NotNull(message = "Unit price is a required field.")
    @Min(value = 1, message = "Price should be at least $1")
    private BigDecimal price;
    @NotNull(message = "Quantity is a required field.")
    @Min(value = 1, message = "Quantity can not be less than 1 or greater than 100.")
    @Max(value = 100, message = "Quantity can not be less than 1 or greater than 100.")
    private Integer quantity;
    @NotNull(message = "Tax is a required field.")
    @Min(value = 0, message = "Tax should be between 0-20")
    @Max(value = 20, message = "Tax should be between 0-20")
    private Integer tax;
    private BigDecimal total;
    private BigDecimal profitLoss;
    private Integer remainingQuantity;
    @Valid
    private InvoiceDto invoice;
    @NotNull(message = "Please select a product.")
    private ProductDto product;
    
    
    
}
