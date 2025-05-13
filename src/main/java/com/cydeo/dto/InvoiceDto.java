package com.cydeo.dto;

import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class InvoiceDto {
    
    private Long id;
    private String invoiceNo;
    private InvoiceStatus invoiceStatus;
    private InvoiceType invoiceType;
    private LocalDate date;
    private CompanyDto companyDto;
    private ClientVendorDto clientVendorDto;
    private BigDecimal price;
    private BigDecimal tax;
    private BigDecimal total;
}
