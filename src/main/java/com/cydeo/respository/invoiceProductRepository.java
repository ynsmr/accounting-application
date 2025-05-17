package com.cydeo.respository;

import com.cydeo.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface invoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {
}
