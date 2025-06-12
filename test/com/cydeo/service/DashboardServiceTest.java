package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.User;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {
    
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private UserService userService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private DashboardServiceImpl dashboardService;
    private Invoice invoice;
    private InvoiceDto invoiceDto;

    @BeforeEach
    void setUp(){
        //Invoice
        invoice = new Invoice();
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice.setInvoiceType(InvoiceType.SALES);
        invoice.setDate(LocalDate.now());
        invoice.setId(1L);

        Company company = new Company();
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setId(1l);
        company.setTitle("title");
        invoice.setCompany(company);

        ClientVendor clientVendor = new ClientVendor();
        clientVendor.setId(1L);
        clientVendor.setClientVendorType(ClientVendorType.CLIENT);
        invoice.setClientVendor(clientVendor);

        //InvoiceDto
        invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceDto.setInvoiceType(InvoiceType.SALES);
        invoiceDto.setDate(LocalDate.now());
        invoiceDto.setId(1L);
        invoiceDto.setTax(BigDecimal.ZERO);
        invoiceDto.setPrice(BigDecimal.ZERO);
        invoiceDto.setTotal(BigDecimal.ZERO);

        CompanyDto companyDto = new CompanyDto();
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto.setId(1l);
        companyDto.setTitle("title");
        invoiceDto.setCompany(companyDto);

        ClientVendorDto clientVendorDto = new ClientVendorDto();
        clientVendorDto.setId(1L);
        clientVendorDto.setClientVendorType(ClientVendorType.CLIENT);
        invoiceDto.setClientVendor(clientVendorDto);
        invoiceDto.setClientVendor(clientVendorDto);

    }

    private List<Invoice> getMultipleInvoices(){
        Invoice invoice1 = new Invoice();
        invoice1.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice1.setInvoiceType(InvoiceType.SALES);
        invoice1.setDate(LocalDate.now());
        invoice1.setId(2L);

        Company company1 = new Company();
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setId(1l);
        company1.setTitle("title");
        invoice1.setCompany(company1);

        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice2.setInvoiceType(InvoiceType.SALES);
        invoice2.setDate(LocalDate.now());
        invoice2.setId(3L);

        invoice2.setCompany(company1);

        return List.of(invoice, invoice1, invoice2);

    }

    private List<InvoiceDto> getMultipleInvoiceDtos(){
        InvoiceDto invoice1 = new InvoiceDto();
        invoice1.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice1.setInvoiceType(InvoiceType.SALES);
        invoice1.setDate(LocalDate.now());
        invoice1.setId(2L);
        invoice1.setPrice(BigDecimal.ZERO);

        CompanyDto company1 = new CompanyDto();
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setId(1l);
        company1.setTitle("title");
        invoice1.setCompany(company1);

        InvoiceDto invoice2 = new InvoiceDto();
        invoice2.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice2.setInvoiceType(InvoiceType.SALES);
        invoice2.setDate(LocalDate.now());
        invoice2.setId(3L);
        invoice2.setPrice(BigDecimal.ZERO);

        invoice2.setCompany(company1);

        return List.of(invoiceDto, invoice1, invoice2);

    }
    
    @Test
    void should_calculate_total_cost_by_company(){
        mockAuthentication();
        List<InvoiceDto> invoices = getMultipleInvoiceDtos();
        invoices.forEach(invoice -> invoice.setInvoiceType(InvoiceType.PURCHASE));
        when(invoiceService.retrieveCurrentPurchaseInvoices()).thenReturn(invoices);

        BigDecimal actualTotalCost = dashboardService.calculateTotalCostByCompanyId();
        
        assertEquals(BigDecimal.ZERO, actualTotalCost);
        
        verify(invoiceService).retrieveCurrentPurchaseInvoices();

    }

    @Test
    void should_calculate_total_sales_by_company(){
        mockAuthentication();
        List<InvoiceDto> invoices = getMultipleInvoiceDtos();
        invoices.forEach(invoice -> {
            invoice.setInvoiceType(InvoiceType.SALES);
            invoice.setTotal(BigDecimal.ZERO);
        });
        when(invoiceService.retrieveCurrentSalesInvoices()).thenReturn(invoices);

        BigDecimal actualTotalSales = dashboardService.calculateTotalSalesByCompanyId();

        assertEquals(BigDecimal.ZERO, actualTotalSales);

        verify(invoiceService).retrieveCurrentSalesInvoices();

    }
    
    

    private void mockAuthentication(){
        User user = new User();
        user.setId(1L);
        user.setFirstname("Mike");
        user.setLastname("Tyson");
        user.setUsername("miketyson");
        user.setAccountNonLocked(true);

        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setInsertDateTime(LocalDateTime.of(2012, 12, 12, 0, 0, 0));
        user.setCompany(company);

        lenient().when(userService.getLoggedInUser()).thenReturn(user);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

    }
    
}
