package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.ref.PhantomReference;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static org.yaml.snakeyaml.nodes.NodeId.sequence;

@Service
@AllArgsConstructor
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceService invoiceService;
    private final MapperUtil mapperUtil;


    @Override
    public List<InvoiceProductDto> listAllInvoiceProducts() {
        return invoiceProductRepository.findAll().stream()
                .map(this::convertToDto)
                .peek(invoiceProductDto -> invoiceProductDto.setTotal(calculateTotal(invoiceProductDto)))
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceProductDto findById(Long invoiceProductId) {
        InvoiceProductDto invoiceProductDto = convertToDto(findInvoiceProductById(invoiceProductId));
        invoiceProductDto.setTotal(calculateTotal(invoiceProductDto));
        return invoiceProductDto;
    }

    @Override
    public void deleteInvoiceProduct(Long invoiceProductId) {
        softDeleteInvoiceProduct(findInvoiceProductById(invoiceProductId));
    }

    @Override
    public void saveInvoiceProduct(InvoiceProductDto invoiceProductDto) {
        invoiceProductRepository.save(convertToEntity(invoiceProductDto));
    }

    @Override
    public void updateInvoiceProduct(InvoiceProductDto invoiceProductDto) {
        invoiceProductRepository.save(convertToEntity(invoiceProductDto));

    }

    @Override
    public void addInvoiceProduct(InvoiceProductDto invoiceProductDto, Long id) {
        invoiceProductDto.setInvoice(invoiceService.findById(id));
        invoiceProductRepository.save(convertToEntity(invoiceProductDto));
    }


    @Override
    public List<InvoiceProductDto> findInvoiceProductsByInvoiceId(Long invoiceId) {
        return invoiceProductRepository.findInvoiceProductsByInvoice_Id(invoiceId).stream()
                .map(this::convertToDto)
                .peek(invoiceProductDto -> invoiceProductDto.setTotal(calculateTotal(invoiceProductDto)))
                .collect(Collectors.toList());
    }

    @Override
    public void removeInvoiceProduct(Long invoiceId, Long invoiceProductId) {
        InvoiceProduct invoiceProductById = findInvoiceProductById(invoiceProductId);
        softDeleteInvoiceProduct(invoiceProductById);
        invoiceService.updateInvoice(invoiceService.findById(invoiceId));

    }

    private InvoiceProductDto convertToDto(InvoiceProduct invoiceProduct) {
        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

    private InvoiceProduct convertToEntity(InvoiceProductDto invoiceProductDto) {
        return mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
    }

    private InvoiceProduct findInvoiceProductById(Long invoiceProductId) {
        return invoiceProductRepository.findById(invoiceProductId)
                .orElseThrow(() -> new NoSuchElementException("No invoice product with Id: " + invoiceProductId));
    }

    private void softDeleteInvoiceProduct(InvoiceProduct invoiceProduct) {
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
    }


    private BigDecimal calculateTotal(InvoiceProductDto invoiceProductDto) {
        BigDecimal price = invoiceProductDto.getPrice();
        Integer taxRate = invoiceProductDto.getTax();
        Integer quantity = invoiceProductDto.getQuantity();

        // Convert tax percentage to multiplier (e.g., 10% becomes 1.10)
        BigDecimal taxMultiplier = BigDecimal.ONE.add(
                BigDecimal.valueOf(taxRate).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32)
        );

        // Calculate total price including tax and quantity
        return price.multiply(taxMultiplier).multiply(BigDecimal.valueOf(quantity));
    }
    
    

}