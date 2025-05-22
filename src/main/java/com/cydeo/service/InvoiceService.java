package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {
    
    List<InvoiceDto> listAllInvoices();
    InvoiceDto findById(Long invoiceId);
    void deleteInvoiceById(Long invoiceId);
    void saveInvoice(InvoiceDto invoiceDto, InvoiceType invoiceType);
    void updateInvoice(InvoiceDto invoiceDto);
    InvoiceDto getInvoiceTemplate(InvoiceType invoiceType);
    boolean clientVendorHasInvoice(Long clientVendorId);
    BigDecimal calculateGrandTotal(Long invoiceId);
    BigDecimal calculateGrandTax(Long invoiceId);
    BigDecimal calculateInvoicePrice(Long invoiceId);
    List<InvoiceDto> retrieveCurrentPurchaseInvoices();
    List<InvoiceDto> retrieveCurrentSalesInvoices();
    void approvePurchaseInvoice(Long invoiceId);
    void approveSalesInvoice(Long invoiceId);
}
