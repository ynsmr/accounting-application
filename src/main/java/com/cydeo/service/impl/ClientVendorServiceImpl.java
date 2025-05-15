package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.respository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientVendorServiceImpl implements ClientVendorService {
    
    private final ClientVendorRepository clientVendorRepository;
    
    @Override
    public ClientVendorDto findById(Long id) {
        return clientVendorRepository.fid;
    }

    @Override
    public List<ClientVendorDto> listAll() {
        return List.of();
    }

    @Override
    public void saveClientVendor(ClientVendorDto clientVendorDto) {

    }

    @Override
    public void updateClientVendor(ClientVendor clientVendor) {

    }

    @Override
    public void deleteClientVendor(Long id) {

    }
}
