package com.cydeo.dto;

import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotNull;
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
    private CompanyDto company;
    @NotNull(message = "Client vendor is a required field.")
    private ClientVendorDto clientVendor;
    private BigDecimal price;
    private BigDecimal tax;
    private BigDecimal total;
    
}
