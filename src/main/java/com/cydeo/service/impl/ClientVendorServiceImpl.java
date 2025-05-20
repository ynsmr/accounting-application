package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClientVendorServiceImpl implements ClientVendorService {
    
    private final ClientVendorRepository clientVendorRepository;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;
    
    @Override
    public ClientVendorDto findById(Long id) {
        return convertToDto(findClientVendorById(id));
    }

    @Override
    public List<ClientVendorDto> listAll() {
        return clientVendorRepository.findAll().stream()
                .filter(clientVendor -> userService.getLoggedInUser().getCompany().getId().equals(clientVendor.getCompany().getId()))
                .sorted(Comparator.comparing(ClientVendor::getClientVendorType).thenComparing(ClientVendor::getClientVendorName))
                .map(this::convertToDto)
                .peek(clientVendorDto -> clientVendorDto.setHasInvoice(invoiceService.clientVendorHasInvoice(clientVendorDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> listAllByType(ClientVendorType clientVendorType) {
        return clientVendorRepository.findAllByClientVendorType(clientVendorType).stream()
                .map(this::convertToDto)
                .peek(clientVendorDto -> clientVendorDto.setHasInvoice(invoiceService.clientVendorHasInvoice(clientVendorDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void saveClientVendor(ClientVendorDto clientVendorDto) {
        clientVendorRepository.save(convertToEntity(clientVendorDto));
    }

    @Override
    public void updateClientVendor(ClientVendorDto clientVendorDto) {
        clientVendorRepository.save(convertToEntity(clientVendorDto));
        
    }

    @Override
    public void deleteClientVendor(Long id) {
        softDeleteClientVendor(findClientVendorById(id));

    }
    

    private ClientVendor findClientVendorById(Long id){
        return clientVendorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such client/vendor found"));
    }
    
    private ClientVendorDto convertToDto(ClientVendor clientVendor){
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }
    
    private ClientVendor convertToEntity(ClientVendorDto clientVendorDto){
        return mapperUtil.convert(clientVendorDto, new ClientVendor());
    }
    
    private void softDeleteClientVendor(ClientVendor clientVendor){
        clientVendor.setIsDeleted(true);
        clientVendorRepository.save(clientVendor);
    }
}
