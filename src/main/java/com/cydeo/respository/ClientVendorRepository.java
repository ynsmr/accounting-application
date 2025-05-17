package com.cydeo.respository;

import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientVendorRepository extends JpaRepository<ClientVendor, Long> {
    
    List<ClientVendor> findAllByClientVendorType(ClientVendorType clientVendorType);
}
