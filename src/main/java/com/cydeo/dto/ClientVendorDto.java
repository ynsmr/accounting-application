package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class ClientVendorDto {
    
    private Long id;
    @NotNull(message = "Name is a required field.")
    @Size(min = 2, max = 50, message = "Client/Vendor name should be 2-50 character long.")
    private String clientVendorName;
    @NotBlank(message = "Phone is a required field.")
    @Pattern(regexp = "^$|^\\d{5}([-]|\\s*)?(\\d{4})?$", message = "Phone number should be in valid format.")
    private String phone;
    @NotBlank(message = "Website is a required field.")
    @Pattern(regexp = "^$|^https?://[a-zA-Z0-9/\\-\\.]+\\.([A-Za-z]{2,5})[a-zA-Z0-9/\\&\\?\\=\\-\\.\\~\\%]*", message = "Website should be in valid format.")
    private String website;
    @NotNull(message = "Please select type.")
    private ClientVendorType clientVendorType;
    @Valid
    private AddressDto address;
    private CompanyDto companyDto;
    private boolean hasInvoice;
}
