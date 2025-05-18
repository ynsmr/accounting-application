package com.cydeo.dto;

import com.cydeo.enums.CompanyStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@NoArgsConstructor
@Getter
@Setter
public class CompanyDto {
    
    private Long id;
    @Size(min = 2, max = 10, message = "Title should be 2-100 characters long.")
    private String title;
    @Pattern(regexp = "^d {5}([-]|s*)?(d {4})?$", message = "Phone number should be in valid format.")
    private String phone;
    @NotBlank(message = "Website is a required field.")
    @Pattern(regexp = "^http(s {0,1})://[a-zA-Z0-9/\\-\\.]+.([A-Za-z/] {2,5})[a-zA-Z0-9/\\&\\?\\=\\-\\.\\~\\%]*", message = "Website should have a valid format.")
    private String website;
    @Valid
    private AddressDto address;
    private CompanyStatus companyStatus;
}
