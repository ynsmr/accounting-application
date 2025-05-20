package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;

import java.util.List;

public interface ClientVendorService {
    
    ClientVendorDto findById(Long id);
    List<ClientVendorDto> listAll();
    List<ClientVendorDto> listAllByType(ClientVendorType clientVendorType);
    void saveClientVendor(ClientVendorDto clientVendorDto);
    void updateClientVendor(ClientVendorDto clientVendorDto);
    void deleteClientVendor(Long id);
    
}
