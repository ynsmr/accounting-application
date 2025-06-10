package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.exception.ProductLowLimitAlert;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.InvoiceProductRepository;
import com.cydeo.respository.InvoiceRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        invoiceById.setClientVendor(convertToEntity(invoiceDto).getClientVendor());
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
        // Process all invoice products
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId);
        for (InvoiceProductDto invoiceProductDto : invoiceProducts) {
            updateProductInventoryForPurchase(invoiceProductDto);
            updateInvoiceProductDetails(invoiceProductDto, invoiceId);
        }

        // Update invoice status
        updateInvoiceStatus(invoiceId);
    }

    private void updateProductInventoryForPurchase(InvoiceProductDto invoiceProductDto) {
        ProductDto product = invoiceProductDto.getProduct();
        // For purchase invoices, we increase the inventory
        product.setQuantityInStock(product.getQuantityInStock() + invoiceProductDto.getQuantity());
        productService.updateProduct(product);
    }

    @Transactional
    @Override
    public void approveSalesInvoice(Long invoiceId) {
        // Process all invoice products
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId);
        for (InvoiceProductDto invoiceProductDto : invoiceProducts) {
            updateProductInventory(invoiceProductDto);
            updateInvoiceProductDetails(invoiceProductDto, invoiceId);
        }

        // Update invoice status
        Invoice invoice = updateInvoiceStatus(invoiceId);
        
        // Calculate and set profit/loss for each invoice product
        // We need to get the actual entities because we need to set the profitLoss field
        List<InvoiceProduct> invoiceProductEntities = invoiceProductRepository.findAllByInvoiceId(invoiceId);
        for (InvoiceProduct invoiceProduct : invoiceProductEntities) {
            calculateAndSetProfitLoss(invoiceProduct);
        }
        
    }

    private Invoice updateInvoiceStatus(Long invoiceId) {
        Invoice invoice = findInvoiceById(invoiceId);
        invoice.setDate(LocalDate.now());
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void calculateProfitLossForAllApprovedSales() {
        List<InvoiceProduct> salesInvoiceProducts = invoiceProductRepository.findAll().stream()
                .filter(ip -> ip.getInvoice().getInvoiceType().equals(InvoiceType.SALES))
                .filter(ip -> ip.getInvoice().getInvoiceStatus().equals(InvoiceStatus.APPROVED))
                .filter(ip -> ip.getInvoice().getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .collect(Collectors.toList());
        
        for (InvoiceProduct salesInvoiceProduct : salesInvoiceProducts) {
            calculateAndSetProfitLoss(salesInvoiceProduct);
        }
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
                .orElseThrow(() -> new InvoiceNotFoundException("No invoice found with id: " + invoiceId));
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
                    .map(invoice -> invoice .getInvoiceNo().split(INVOICE_NUMBER_SEPARATOR)[1])
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        }

        String SEQUENCE_NUMBER_LENGTH = "3";
        return String.format("%0" + SEQUENCE_NUMBER_LENGTH + "d", nextSequence);
    }

    private BigDecimal calculateProfitLoss(Integer quantitySold, Queue<InvoiceProduct> purchaseQ, Queue<InvoiceProduct> salesQ) {

        return salesQ.poll().getPrice().subtract(purchaseQ.poll().getPrice()).multiply(BigDecimal.valueOf(quantitySold));
    }

    private Queue<InvoiceProduct> getPurchaseQ(Long productId) {
        return invoiceProductRepository.findAll().stream()
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceType().equals(InvoiceType.PURCHASE))
                .filter(invoiceProduct -> invoiceProduct.getProduct().getId().equals(productId))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceStatus().equals(InvoiceStatus.AWAITING_APPROVAL))
                .sorted(Comparator.comparing((InvoiceProduct invoiceProduct) -> invoiceProduct.getInvoice().getDate())).collect(Collectors.toCollection(LinkedList::new));

    }

    private Queue<InvoiceProduct> getSalesQ(Long productId) {
        return invoiceProductRepository.findAll().stream()
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceType().equals(InvoiceType.SALES))
                .filter(invoiceProduct -> invoiceProduct.getProduct().getId().equals(productId))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceStatus().equals(InvoiceStatus.AWAITING_APPROVAL))
                .sorted(Comparator.comparing((InvoiceProduct invoiceProduct) -> invoiceProduct.getInvoice().getDate())).collect(Collectors.toCollection(LinkedList::new));

    }


    private void updateProductInventory(InvoiceProductDto invoiceProductDto) {
        ProductDto product = invoiceProductDto.getProduct();
        
        // Update product quantity
        int newQuantity = product.getQuantityInStock() - invoiceProductDto.getQuantity();
        if (newQuantity <= 0){
            throw new ProductNotFoundException("There is no enough stock of " + product.getName() + " please update the invoice");
        }
        product.setQuantityInStock(newQuantity);
        productService.updateProduct(product);
        if (newQuantity < invoiceProductDto.getProduct().getLowLimitAlert()){
            throw new ProductLowLimitAlert("Product " + invoiceProductDto.getProduct().getName()+ " has now fallen below the low limit set: " + invoiceProductDto.getProduct().getLowLimitAlert());
        }
    }

    private void updateInvoiceProductDetails(InvoiceProductDto invoiceProductDto, Long invoiceId) {
        invoiceProductDto.setInvoice(findById(invoiceId));
        invoiceProductService.saveInvoiceProduct(invoiceProductDto);
    }


    private void calculateAndSetProfitLoss(InvoiceProduct salesInvoiceProduct) {
        if (salesInvoiceProduct.getInvoice().getInvoiceType() != InvoiceType.SALES) {
            // Only calculate profit/loss for sales invoice products
            return;
        }
        
        Long productId = salesInvoiceProduct.getProduct().getId();
        int quantityToMatch = salesInvoiceProduct.getQuantity();
        BigDecimal salePrice = salesInvoiceProduct.getPrice();
        BigDecimal totalProfitLoss = BigDecimal.ZERO;
        
        // Get a queue of purchase invoice products for this product, sorted by date (FIFO)
        Queue<InvoiceProduct> purchaseQ = getPurchaseQForProfitLoss(productId);
        
        // Match this sale with purchases using FIFO
        while (quantityToMatch > 0 && !purchaseQ.isEmpty()) {
            InvoiceProduct purchase = purchaseQ.peek();
            int availablePurchaseQuantity = purchase.getQuantity();
            BigDecimal purchasePrice = purchase.getPrice();
            
            // Determine how many units to match with this purchase
            int unitsToMatch = Math.min(quantityToMatch, availablePurchaseQuantity);
            
            // Calculate profit/loss for this match
            BigDecimal profitPerUnit = salePrice.subtract(purchasePrice);
            BigDecimal profitForMatch = profitPerUnit.multiply(BigDecimal.valueOf(unitsToMatch));
            totalProfitLoss = totalProfitLoss.add(profitForMatch);
            
            // Update remaining quantities
            quantityToMatch -= unitsToMatch;
            
            if (unitsToMatch >= availablePurchaseQuantity) {
                // We've used up this purchase, remove it
                purchaseQ.poll();
            } else {
                // We've only used part of this purchase
                purchase.setQuantity(availablePurchaseQuantity - unitsToMatch);
            }
        }
        
        // Set the profit/loss field on the sales invoice product
        salesInvoiceProduct.setProfitLoss(totalProfitLoss.round(MathContext.DECIMAL32));
        invoiceProductRepository.save(salesInvoiceProduct);
    }
    
    private Queue<InvoiceProduct> getPurchaseQForProfitLoss(Long productId) {
        return invoiceProductRepository.findAll().stream()
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceType().equals(InvoiceType.PURCHASE))
                .filter(invoiceProduct -> invoiceProduct.getProduct().getId().equals(productId))
                .filter(invoiceProduct -> invoiceProduct.getInvoice().getInvoiceStatus().equals(InvoiceStatus.APPROVED))
                .sorted(Comparator.comparing((InvoiceProduct invoiceProduct) -> invoiceProduct.getInvoice().getDate()))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}