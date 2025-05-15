package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;

import java.util.List;

public interface ClientVendorService {
    
    ClientVendorDto findClientVendorById(Long id);
    List<ClientVendorDto> listAll();
    void saveClientVendor(ClientVendorDto clientVendorDto);
    void updateClientVendor(ClientVendor clientVendor);
    void deleteClientVendor(Long id);
}
