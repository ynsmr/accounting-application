package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "invoices")
public class Invoice extends BaseEntity {
    
    
    private String invoiceNo;
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;
    private LocalDate date;
    @ManyToOne
    private ClientVendor clientVendor;
    @ManyToOne
    private Company company;
    
    
}
