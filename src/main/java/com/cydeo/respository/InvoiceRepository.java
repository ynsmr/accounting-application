package com.cydeo.respository;

import com.cydeo.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    List<Invoice> findByInvoiceNoStartingWith(String keyword);
    boolean existsByClientVendor_Id(Long clientVendorId);
}
