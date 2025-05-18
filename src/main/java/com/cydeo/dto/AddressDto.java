package com.cydeo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class AddressDto {
    
    private Long id;
    @NotBlank(message = "Address is a required field.")
    @Size(min = 2, max = 100, message = "Should be between 2 to 100 character long.")
    private String addressLine1;
    @Size(max = 100, message = "Can not have more than 100 characters.")
    private String addressLine2;
    @NotBlank(message = "City is a required field")
    @Size(min = 2, max = 50, message = "City name should be 2-50 character long.")
    private String city;
    @NotBlank(message = "Country is a required field.")
    private String country;
    private String state;
    @NotBlank(message = "Zipcode is a required field")
    @Pattern(regexp = "^d {5}([-]|s*)?(d {4})?$", message = "Zipcode should have a valid form.")
    private String zipCode;
}
