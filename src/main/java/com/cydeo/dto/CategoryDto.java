package com.cydeo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CategoryDto {
    
    private Long id;
    private String description;
    private CompanyDto companyDto;
    private boolean hasProduct;
    
}
