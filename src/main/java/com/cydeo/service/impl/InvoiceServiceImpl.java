package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.InvoiceProductRepository;
import com.cydeo.respository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final String INVOICE_NUMBER_SEPARATOR = "-";
    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductService invoiceProductService;
    private final UserService userService;
    private final ProductService productService;
    private final MapperUtil mapperUtil;
    private final InvoiceProductRepository invoiceProductRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, @Lazy InvoiceProductService invoiceProductService, UserService userService, @Lazy ProductService productService, MapperUtil mapperUtil, InvoiceProductRepository invoiceProductRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductService = invoiceProductService;
        this.userService = userService;
        this.productService = productService;
        this.mapperUtil = mapperUtil;
        this.invoiceProductRepository = invoiceProductRepository;
    }

    @Override
    public List<InvoiceDto> listAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDto)
                .peek(invoiceDto -> {
                    invoiceDto.setTotal(calculateGrandTotal(invoiceDto.getId()));
                    invoiceDto.setTax(calculateGrandTax(invoiceDto.getId()));
                    invoiceDto.setPrice(calculateInvoicePrice(invoiceDto.getId()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto findById(Long invoiceId) {
        InvoiceDto invoiceDto = convertToDto(findInvoiceById(invoiceId));
        invoiceDto.setTotal(calculateGrandTotal(invoiceId));
        invoiceDto.setPrice(calculateInvoicePrice(invoiceId));
        invoiceDto.setTax(calculateGrandTax(invoiceId));
        return invoiceDto;
    }

    @Override
    public void deleteInvoiceById(Long invoiceId) {
        softDeleteInvoice(findInvoiceById(invoiceId));
    }

    @Override
    public void saveInvoice(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        invoiceDto.setInvoiceType(invoiceType);
        invoiceDto.setCompany(mapperUtil.convert(userService.getLoggedInUser().getCompany(), new CompanyDto()));
        invoiceDto.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceRepository.save(convertToEntity(invoiceDto));
    }

    @Override
    public void updateInvoice(InvoiceDto invoiceDto) {
        Invoice invoiceById = findInvoiceById(invoiceDto.getId());
        invoiceById.setClientVendor(invoiceById.getClientVendor());
        invoiceRepository.save(invoiceById);

    }


    @Override
    public InvoiceDto getInvoiceTemplate(InvoiceType invoiceType) {
        InvoiceDto template = new InvoiceDto();
        template.setInvoiceNo(generateInvoiceNumber(invoiceType));
        template.setDate(LocalDate.now());
        return template;
    }

    @Override
    public BigDecimal calculateGrandTotal(Long invoiceId) {
        return invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId).stream()
                .map(InvoiceProductDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add).round(MathContext.DECIMAL32);
    }

    @Override
    public BigDecimal calculateGrandTax(Long invoiceId) {
        // Calculate total without tax
        BigDecimal totalWithoutTax = calculateInvoicePrice(invoiceId);

        // Get the grand total with tax
        BigDecimal total = calculateGrandTotal(invoiceId).round(MathContext.DECIMAL32);
        return total.subtract(totalWithoutTax);

    }

    @Override
    public BigDecimal calculateInvoicePrice(Long invoiceId) {
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId);
        // Calculate total without tax
        return invoiceProducts.stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).round(MathContext.DECIMAL32);
    }

    @Override
    public List<InvoiceDto> retrieveCurrentPurchaseInvoices() {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoice -> invoice.getInvoiceType().equals(InvoiceType.PURCHASE))
                .map(this::convertToDto)
                .peek(invoiceDto -> {
                    invoiceDto.setTotal(calculateGrandTotal(invoiceDto.getId()));
                    invoiceDto.setTax(calculateGrandTax(invoiceDto.getId()));
                    invoiceDto.setPrice(calculateInvoicePrice(invoiceDto.getId()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> retrieveCurrentSalesInvoices() {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoice -> invoice.getInvoiceType().equals(InvoiceType.SALES))
                .map(this::convertToDto)
                .peek(invoiceDto -> {
            invoiceDto.setTotal(calculateGrandTotal(invoiceDto.getId()));
            invoiceDto.setTax(calculateGrandTax(invoiceDto.getId()));
            invoiceDto.setPrice(calculateInvoicePrice(invoiceDto.getId()));
        })
                .collect(Collectors.toList());
    }

    @Override
    public void approvePurchaseInvoice(Long invoiceId) {
        invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId)
        .forEach(invoiceProductDto -> { 
            ProductDto product = invoiceProductDto.getProduct();
            product.setQuantityInStock(product.getQuantityInStock() + invoiceProductDto.getQuantity());
            productService.updateProduct(product);
            invoiceProductDto.setProduct(product);
            invoiceProductDto.setInvoice(findById(invoiceId));
            invoiceProductService.saveInvoiceProduct(invoiceProductDto);
        });
    
    Invoice invoiceById = findInvoiceById(invoiceId);
    invoiceById.setDate(LocalDate.now());
    invoiceById.setInvoiceStatus(InvoiceStatus.APPROVED);
    invoiceRepository.save(invoiceById);
}

    @Override
    public void approveSalesInvoice(Long invoiceId) {
        invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId)
                .forEach(invoiceProductDto -> {
                    ProductDto product = invoiceProductDto.getProduct();
                    product.setQuantityInStock(product.getQuantityInStock() - invoiceProductDto.getQuantity());
                    productService.updateProduct(product);
                    invoiceProductDto.setProduct(product);
                    invoiceProductDto.setProfitLoss(calculateProfitLoss(invoiceProductDto.getQuantity(), product.getId()));
                    invoiceProductDto.setInvoice(findById(invoiceId));
                    invoiceProductService.saveInvoiceProduct(invoiceProductDto);
                });
        Invoice invoiceById = findInvoiceById(invoiceId);
        invoiceById.setDate(LocalDate.now());
        invoiceById.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceRepository.save(invoiceById);
    }

    @Override
    public List<InvoiceDto> listLast3Approved() {
        return listAllInvoices().stream()
                .filter(invoiceDto -> invoiceDto.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoiceDto -> invoiceDto.getInvoiceStatus().equals(InvoiceStatus.APPROVED))
                .sorted(Comparator.comparing(InvoiceDto::getDate).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public boolean clientVendorHasInvoice(Long clientVendorId) {
        return invoiceRepository.existsByClientVendor_Id(clientVendorId);
    }

    private InvoiceDto convertToDto(Invoice invoice) {
        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    private Invoice convertToEntity(InvoiceDto invoiceDto) {
        return mapperUtil.convert(invoiceDto, new Invoice());
    }

    private Invoice findInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NoSuchElementException("No invoice found with id: " + invoiceId));
    }

    private void softDeleteInvoice(Invoice invoice) {
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber(InvoiceType invoiceType) {
        String prefix = invoiceType == InvoiceType.SALES ? "S" : "P";
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
    
    private BigDecimal calculateProfitLoss(Integer quantitySold, Long productId){
        List<InvoiceProduct> purchaseInvoiceProducts = invoiceProductRepository.findAll().stream()
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceType().equals(InvoiceType.PURCHASE))
                .filter(invoiceProduct -> invoiceProduct.getProduct().getId().equals(productId))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceStatus().equals(InvoiceStatus.AWAITING_APPROVAL))
                .sorted(Comparator.comparing((InvoiceProduct invoiceProduct) -> invoiceProduct.getInvoice().getDate()))
                .collect(Collectors.toList());
        
        Queue<InvoiceProduct> purchaseQ = new LinkedList<>(purchaseInvoiceProducts);
        
        
        List<InvoiceProduct> salesInvoiceProducts = invoiceProductRepository.findAll().stream()
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceType().equals(InvoiceType.SALES))
                .filter(invoiceProduct -> invoiceProduct.getProduct().getId().equals(productId))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceStatus().equals(InvoiceStatus.AWAITING_APPROVAL))
                .sorted(Comparator.comparing((InvoiceProduct invoiceProduct) -> invoiceProduct.getInvoice().getDate()))
                .collect(Collectors.toList());
        Queue<InvoiceProduct> salesQ = new LinkedList<>(salesInvoiceProducts);

        
        return salesQ.remove().getPrice().subtract(purchaseQ.remove().getPrice()).multiply(BigDecimal.valueOf(quantitySold));

    }

}