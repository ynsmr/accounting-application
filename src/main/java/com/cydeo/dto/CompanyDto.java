package com.cydeo.dto;

import com.cydeo.enums.CompanyStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@NoArgsConstructor
@Data
public class CompanyDto {
    
    private Long id;
    private String title;
    private String phone;
    private String website;
    private AddressDto addressDto;
    private CompanyStatus companyStatus;
}
