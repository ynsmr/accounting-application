package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.*;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.enums.ProductUnit;
import com.cydeo.exception.InvoiceProductNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.InvoiceProductRepository;
import com.cydeo.service.impl.InvoiceProductServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceProductServiceTest {
    @Mock
    private InvoiceProductRepository invoiceProductRepository;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private InvoiceProductServiceImpl invoiceProductService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    
    private InvoiceProduct invoiceProduct;
    private InvoiceProductDto invoiceProductDto;
    private Invoice invoice;
    private InvoiceDto invoiceDto;
    private Company company;
    private CompanyDto companyDto;
    
    @BeforeEach
    void setUp(){
        invoiceProduct = new InvoiceProduct();
        invoiceProduct.setId(1L);
        invoiceProduct.setPrice(BigDecimal.valueOf(145));
        invoiceProduct.setQuantity(4);

        Product product = new Product();
        product.setId(1L);
        product.setProductUnit(ProductUnit.PCS);
        product.setName("product");
        invoiceProduct.setProduct(product);
        invoiceProduct.setTax(0);
        
        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceType(InvoiceType.SALES);
        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceProduct.setInvoice(invoice);
        invoiceProduct.setRemainingQuantity(0);
        
        company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setTitle("company");
        invoice.setCompany(company);

        invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(1L);
        invoiceProductDto.setPrice(BigDecimal.valueOf(145));
        invoiceProductDto.setQuantity(4);

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setProductUnit(ProductUnit.PCS);
        productDto.setName("product");
        invoiceProductDto.setProduct(productDto);

        invoiceDto = new InvoiceDto();
        invoiceDto.setId(1L);
        invoiceDto.setInvoiceType(InvoiceType.SALES);
        invoiceDto.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoiceDto.setPrice(invoiceProductDto.getPrice());
        //invoiceDto.setTotal(invoiceProductDto.getPrice().multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity())));
        invoiceDto.setTax(BigDecimal.ZERO);
        invoiceProductDto.setInvoice(invoiceDto);
        invoiceProductDto.setRemainingQuantity(0);
        invoiceProductDto.setTotal(invoiceProductDto.getPrice().multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity())));
        invoiceProductDto.setTax(0);

        companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto.setTitle("company");
        invoiceDto.setCompany(companyDto);
        
    }
    
    private List<InvoiceProduct> getMultipleInvoiceProducts(){
        InvoiceProduct invoiceProduct1 = new InvoiceProduct();
        invoiceProduct1.setId(2L);
        invoiceProduct1.setPrice(BigDecimal.valueOf(145));
        invoiceProduct1.setQuantity(4);
        invoiceProduct1.setRemainingQuantity(0);
        invoiceProduct1.setTax(0);

        Product product = new Product();
        product.setId(1L);
        product.setProductUnit(ProductUnit.PCS);
        product.setName("product");
        invoiceProduct1.setProduct(product);
        invoiceProduct1.setInvoice(invoice);

        InvoiceProduct invoiceProduct2 = new InvoiceProduct();
        invoiceProduct2.setId(3L);
        invoiceProduct2.setPrice(BigDecimal.valueOf(145));
        invoiceProduct2.setQuantity(4);
        invoiceProduct2.setProduct(product);
        invoiceProduct2.setRemainingQuantity(0);
        invoiceProduct2.setInvoice(invoice);
        
        return List.of(invoiceProduct, invoiceProduct1, invoiceProduct2);
    }

    private List<InvoiceProductDto> getMultipleInvoiceProductDtos(){
        InvoiceProductDto invoiceProduct1 = new InvoiceProductDto();
        invoiceProduct1.setId(2L);
        invoiceProduct1.setPrice(BigDecimal.valueOf(145));
        invoiceProduct1.setQuantity(4);
        invoiceProduct1.setRemainingQuantity(0);
        invoiceProduct1.setTotal(invoiceProductDto.getPrice().multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity())));
        invoiceProduct1.setTax(0);

        ProductDto product = new ProductDto();
        product.setId(1L);
        product.setProductUnit(ProductUnit.PCS);
        product.setName("product");
        invoiceProduct1.setProduct(product);
        invoiceProduct1.setInvoice(invoiceDto);

        InvoiceProductDto invoiceProduct2 = new InvoiceProductDto();
        invoiceProduct2.setId(3L);
        invoiceProduct2.setPrice(BigDecimal.valueOf(145));
        invoiceProduct2.setQuantity(4);
        invoiceProduct2.setProduct(product);
        invoiceProduct2.setInvoice(invoiceDto);
        invoiceProduct2.setRemainingQuantity(0);
        invoiceProduct2.setTotal(invoiceProductDto.getPrice().multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity())));
        invoiceProduct2.setTax(0);

        return List.of(invoiceProductDto, invoiceProduct1, invoiceProduct2);
    }
    
    @Test
    void should_list_all_invoice_products(){
        mockAuthentication();
        when(invoiceProductRepository.findAll()).thenReturn(getMultipleInvoiceProducts());

        List<InvoiceProductDto> actualInvoiceProducts = invoiceProductService.listAllInvoiceProducts();
        List<InvoiceProductDto> expectedInvoiceProducts = getMultipleInvoiceProductDtos();
        
        assertThat(actualInvoiceProducts).usingRecursiveComparison().isEqualTo(expectedInvoiceProducts);
        verify(invoiceProductRepository).findAll();
    }
    
    @Test
    void should_find_by_id(){
        when(invoiceProductRepository.findById(anyLong())).thenReturn(Optional.of(invoiceProduct));

        InvoiceProductDto actualInvoiceProduct = invoiceProductService.findById(invoiceProduct.getId());
        InvoiceProductDto expectedInvoiceProduct = invoiceProductDto;
        
        assertThat(actualInvoiceProduct).usingRecursiveComparison().isEqualTo(expectedInvoiceProduct);
        verify(invoiceProductRepository).findById(invoice.getId());
    }
    
    @Test
    void should_not_find_by_id(){
        when(invoiceProductRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        Throwable throwable = catchThrowable(() -> invoiceProductService.findById(invoice.getId()));
        
        assertInstanceOf(InvoiceProductNotFoundException.class, throwable);
        
        assertEquals("No invoice product with Id: " + invoice.getId(), throwable.getMessage());
        verify(invoiceProductRepository).findById(invoice.getId());

    }
    
    @Test
    void should_delete_invoice_product(){
        when(invoiceProductRepository.findById(anyLong())).thenReturn(Optional.of(invoiceProduct));
        
        invoiceProductService.deleteInvoiceProduct(invoice.getId());
        
        assertTrue(invoiceProduct.getIsDeleted());
        
        verify(invoiceProductRepository).save(invoiceProduct);
    }
    
    @Test
    void should_save_invoice_product(){
        when(invoiceProductRepository.save(any())).thenReturn(invoiceProduct);
        
        invoiceProductService.saveInvoiceProduct(invoiceProductDto);
        
        verify(invoiceProductRepository).save(invoiceProduct);
    }
    
    @Test
    void should_update_invoice_product(){
        when(invoiceProductRepository.save(any())).thenReturn(invoiceProduct);
        
        invoiceProductService.updateInvoiceProduct(invoiceProductDto);
        
        verify(invoiceProductRepository).save(invoiceProduct);
    }
    
    @Test
    void should_add_invoice_product(){
        when(invoiceService.findById(anyLong())).thenReturn(invoiceDto);
        when(invoiceProductRepository.save(any())).thenReturn(invoiceProduct);
        
        invoiceProductService.addInvoiceProduct(invoiceProductDto, invoiceDto.getId());
        
        assertEquals(invoice.getId(), invoiceProduct.getInvoice().getId());
        verify(invoiceProductRepository).save(invoiceProduct);
    }
    
    @Test
    void should_find_invoice_products_by_invoice_id(){
        when(invoiceProductRepository.findInvoiceProductsByInvoice_Id(anyLong())).thenReturn(getMultipleInvoiceProducts());

        List<InvoiceProductDto> actualInvoiceProducts = invoiceProductService.findInvoiceProductsByInvoiceId(invoice.getId());
        List<InvoiceProductDto> expectedInvoiceProducts = getMultipleInvoiceProductDtos();
        
        assertThat(actualInvoiceProducts).usingRecursiveComparison().isEqualTo(expectedInvoiceProducts);
        verify(invoiceProductRepository).findInvoiceProductsByInvoice_Id(invoice.getId());
    }
    
    @Test
    void should_remove_invoice_product(){
        when(invoiceProductRepository.findById(anyLong())).thenReturn(Optional.of(invoiceProduct));
        
        
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
