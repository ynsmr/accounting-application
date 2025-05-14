package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.CompanyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "companies")
@Where(clause = "is_deleted = false")
public class Company extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String title;
    private String phone;
    private String website;
    @Enumerated(EnumType.STRING)
    private CompanyStatus companyStatus;
    @OneToOne
    private Address address;

}
