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
import com.cydeo.exception.InvoiceNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.InvoiceProductRepository;
import com.cydeo.respository.InvoiceRepository;
import com.cydeo.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private InvoiceProductService invoiceProductService;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private InvoiceProductRepository invoiceProductRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    @InjectMocks
    private InvoiceServiceImpl invoiceService;
    
    Invoice invoice;
    InvoiceDto invoiceDto;
    
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
        clientVendor.setId(1L);
        clientVendor.setClientVendorType(ClientVendorType.CLIENT);
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
        company1.setId(2l);
        company1.setTitle("title");
        invoice1.setCompany(company1);

        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice2.setInvoiceType(InvoiceType.SALES);
        invoice2.setDate(LocalDate.now());
        invoice2.setId(3L);

        Company company2 = new Company();
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setId(3l);
        company2.setTitle("title");
        invoice2.setCompany(company2);
        
        return List.of(invoice, invoice1, invoice2);
        
    }

    private List<InvoiceDto> getMultipleInvoiceDtos(){
        InvoiceDto invoice1 = new InvoiceDto();
        invoice1.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice1.setInvoiceType(InvoiceType.SALES);
        invoice1.setDate(LocalDate.now());
        invoice1.setId(2L);

        CompanyDto company1 = new CompanyDto();
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setId(2l);
        company1.setTitle("title");
        invoice1.setCompany(company1);

        InvoiceDto invoice2 = new InvoiceDto();
        invoice2.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice2.setInvoiceType(InvoiceType.SALES);
        invoice2.setDate(LocalDate.now());
        invoice2.setId(3L);

        CompanyDto company2 = new CompanyDto();
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setId(3l);
        company2.setTitle("title");
        invoice2.setCompany(company2);

        return List.of(invoiceDto, invoice1, invoice2);

    }
    
    @Test
    void should_list_all_invoices(){
        when(invoiceRepository.findAll()).thenReturn(getMultipleInvoices());

        List<InvoiceDto> actualInvoices = invoiceService.listAllInvoices();
        List<InvoiceDto> expectedInvoices = getMultipleInvoiceDtos().stream()
                .peek(invoiceDto -> {
            invoiceDto.setTax(BigDecimal.ZERO); 
            invoiceDto.setPrice(BigDecimal.ZERO); 
            invoiceDto.setTotal(BigDecimal.ZERO);
        }).collect(Collectors.toList());
        
        assertThat(actualInvoices).usingRecursiveComparison().isEqualTo(expectedInvoices);
        verify(invoiceRepository).findAll();
    }
    
    @Test
    void should_find_by_id(){
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));

        InvoiceDto actualInvoice = invoiceService.findById(invoice.getId());
        InvoiceDto expectedInvoice = invoiceDto;
        
        assertThat(actualInvoice).usingRecursiveComparison().isEqualTo(expectedInvoice);
        verify(invoiceRepository).findById(invoice.getId());
    }
    
    @Test
    void should_not_find_by_id(){
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        Throwable throwable = catchThrowable(() -> invoiceService.findById(invoice.getId()));
        
        assertInstanceOf(InvoiceNotFoundException.class, throwable);
        
        assertEquals("No invoice found with id: " + invoice.getId(), throwable.getMessage());
    }
    
    @Test
    void should_delete_invoice_by_id(){
        when(invoiceRepository.save(any())).thenReturn(invoice);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        
        invoiceService.deleteInvoiceById(invoice.getId());
        
        assertTrue(invoice.getIsDeleted());
        verify(invoiceRepository).save(invoice);
    }
    
    @Test
    void should_save_invoice(){
        mockAuthentication();
        when(invoiceRepository.save(any())).thenReturn(invoice);
        
        invoiceService.saveInvoice(invoiceDto, invoiceDto.getInvoiceType());
        
        assertEquals(BigDecimal.ZERO, invoiceDto.getTotal());
        assertEquals(BigDecimal.ZERO, invoiceDto.getTax());
        assertEquals(BigDecimal.ZERO, invoiceDto.getPrice());
        
        verify(invoiceRepository).save(invoice);
    }
    
    @Test
    void should_update_invoice(){
        when(invoiceRepository.save(any())).thenReturn(invoice);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(invoice));
        
        invoiceService.updateInvoice(invoiceDto);
        
        assertEquals(invoiceDto.getClientVendor().getId() ,invoice.getClientVendor().getId());
        
        verify(invoiceRepository).save(invoice);
        
    }
    
    @Test
    void should_get_invoice_template_sales(){
        
        InvoiceDto invoiceTemplate = invoiceService.getInvoiceTemplate(InvoiceType.SALES);
        assertTrue(invoiceTemplate.getInvoiceNo().startsWith("S"));
    }

    @Test
    void should_get_invoice_template_purchase(){

        InvoiceDto invoiceTemplate = invoiceService.getInvoiceTemplate(InvoiceType.PURCHASE);
        assertTrue(invoiceTemplate.getInvoiceNo().startsWith("P"));
    }
    
    @Test
    void should_calculate_grand_total(){
        
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
        user.setCompany(company);

        lenient().when(userService.getLoggedInUser()).thenReturn(user);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

    }
    
    
    
    
    
}
