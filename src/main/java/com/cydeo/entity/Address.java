package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "addresses")
@Where(clause = "is_deleted = false")
public class Address extends BaseEntity {
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    
    
    
}
