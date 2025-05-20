package com.cydeo.dto;

import com.cydeo.annotation.UniqueDescription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class CategoryDto {
    
    private Long id;
    @NotBlank(message = "Description is a requied field.")
    @Size(min = 2, max = 50, message = "Description should be 2-50 character long.")
    @UniqueDescription
    private String description;
    private CompanyDto company;
    private boolean hasProduct;

    
}
