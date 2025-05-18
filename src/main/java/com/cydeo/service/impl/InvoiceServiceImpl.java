package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.InvoiceRepository;
import com.cydeo.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final String INVOICE_NUMBER_SEPARATOR = "-";
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    

    @Override
    public List<InvoiceDto> listAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto findById(Long invoiceId) {
        return convertToDto(findInvoiceById(invoiceId));
    }

    @Override
    public void deleteInvoiceById(Long invoiceId) {
        softDeleteInvoice(findInvoiceById(invoiceId));
    }

    @Override
    public void saveInvoice(InvoiceDto invoiceDto) {
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceRepository.save(convertToEntity(invoiceDto));
    }

    @Override
    public void updateInvoice(InvoiceDto invoiceDto) {
        invoiceDto.setInvoiceStatus(findInvoiceById(invoiceDto.getId()).getInvoiceStatus());
        invoiceRepository.save(convertToEntity(invoiceDto));

    }
    

    @Override
    public InvoiceDto getInvoiceTemplate(ClientVendorType clientVendorType) {
        InvoiceDto template = new InvoiceDto();
        template.setInvoiceNo(generateInvoiceNumber(clientVendorType));
        template.setDate(LocalDate.now());
        return template;
    }

    private InvoiceDto convertToDto(Invoice invoice){
        return mapperUtil.convert(invoice, new InvoiceDto());
    }
    
    private Invoice convertToEntity(InvoiceDto invoiceDto){
       return mapperUtil.convert(invoiceDto, new Invoice());
    }
    
    private Invoice findInvoiceById(Long invoiceId){
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NoSuchElementException("No invoice found with id: " + invoiceId));
    }
    
    private void softDeleteInvoice(Invoice invoice){
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber(ClientVendorType clientVendorType) {
        String prefix = clientVendorType == ClientVendorType.CLIENT ? "S" : "P";
        String formattedSequence = getNextInvoiceSequence(prefix);
        return prefix + INVOICE_NUMBER_SEPARATOR + formattedSequence;
    }

    private String getNextInvoiceSequence(String prefix) {
        List<Invoice> invoices = invoiceRepository.findByInvoiceNoStartingWith(prefix + INVOICE_NUMBER_SEPARATOR);

        int nextSequence = 1;

        if (!invoices.isEmpty()) {
            nextSequence = invoices.stream()
                    .map(invoice -> invoice.getInvoiceNo().split(INVOICE_NUMBER_SEPARATOR)[1])
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        }

        String SEQUENCE_NUMBER_LENGTH = "3";
        return String.format("%0" + SEQUENCE_NUMBER_LENGTH + "d", nextSequence);
    }

}
