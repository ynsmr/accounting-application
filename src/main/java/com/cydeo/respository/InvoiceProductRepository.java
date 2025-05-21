package com.cydeo.respository;

import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {
    
    List<InvoiceProduct> findInvoiceProductsByInvoice_Id(Long invoiceId);

    List<InvoiceProduct> findByInvoice_Company_Id(Long invoiceCompanyId);
    
}
