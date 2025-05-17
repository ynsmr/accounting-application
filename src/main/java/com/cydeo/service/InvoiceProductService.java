package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;

import java.util.List;

public interface InvoiceProductService {
    
    List<InvoiceProductDto> listAllInvoiceProducts();
    InvoiceProductDto findById(Long invoiceProductId);
    void deleteInvoiceProduct(Long invoiceProduct);
    void saveInvoiceProduct(InvoiceProductDto invoiceProductDto);
    void updateInvoiceProduct(InvoiceProductDto invoiceProductDto);
    List<InvoiceProductDto> findInvoiceProductsByInvoiceId(Long invoiceId);
}
