package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "invoice_products")
@Where(clause = "is_deleted = false")
public class InvoiceProduct extends BaseEntity {
    
    private int quantity;
    private BigDecimal price;
    private int tax;
    private int remainingQuantity;
    private BigDecimal profitLoss;
    @ManyToOne
    private Invoice invoice;
    @ManyToOne
    private Product product;
}
