package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.ClientVendorType;

import java.util.List;

public interface InvoiceService {
    
    List<InvoiceDto> listAllInvoices();
    InvoiceDto findById(Long invoiceId);
    void deleteInvoiceById(Long invoiceId);
    void saveInvoice(InvoiceDto invoiceDto);
    void updateInvoice(InvoiceDto invoiceDto);
    InvoiceDto getInvoiceTemplate(ClientVendorType clientVendorType);
}
