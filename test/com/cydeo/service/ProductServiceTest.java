package com.cydeo.service;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import com.cydeo.entity.Product;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.ProductUnit;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.ProductRepository;
import com.cydeo.service.impl.ProductServiceImpl;
import org.checkerframework.checker.units.qual.C;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InvoiceProductService invoiceProductService;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private ProductServiceImpl productService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    
    private Product product;
    private ProductDto productDto;
    
    @BeforeEach
    void setUp(){
        product = new Product();
        product.setId(1L);
        product.setProductUnit(ProductUnit.PCS);
        product.setName("product");
        product.setLowLimitAlert(15);
        product.setQuantityInStock(24);

        Category category = new Category();
        category.setDescription("category");
        category.setId(1L);
        product.setCategory(category);
        
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setProductUnit(ProductUnit.PCS);
        productDto.setName("product");
        productDto.setLowLimitAlert(15);
        productDto.setQuantityInStock(24);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setDescription("category");
        categoryDto.setId(1L);
        productDto.setCategory(categoryDto);
    }
    
    private List<Product> getMultipleProducts(){
        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductUnit(ProductUnit.PCS);
        product2.setName("product2");
        product2.setLowLimitAlert(25);
        product2.setQuantityInStock(54);

        Category category2 = new Category();
        category2.setDescription("category2");
        category2.setId(2L);
        product2.setCategory(category2);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setProductUnit(ProductUnit.PCS);
        product3.setName("product3");
        product3.setLowLimitAlert(25);
        product3.setQuantityInStock(54);

        Category category3 = new Category();
        category3.setDescription("category3");
        category3.setId(3L);
        product3.setCategory(category3);
        
        return List.of(product, product2, product3);
    }

    private List<ProductDto> getMultipleProductDtos(){
        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        productDto2.setProductUnit(ProductUnit.PCS);
        productDto2.setName("product2");
        productDto2.setLowLimitAlert(25);
        productDto2.setQuantityInStock(54);
        
        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setDescription("category2");
        productDto2.setCategory(categoryDto2);

        ProductDto productDto3 = new ProductDto();
        productDto3.setId(3L);
        productDto3.setProductUnit(ProductUnit.PCS);
        productDto3.setName("product3");
        productDto3.setLowLimitAlert(25);
        productDto3.setQuantityInStock(54);

        CategoryDto categoryDto3 = new CategoryDto();
        categoryDto3.setId(3L);
        categoryDto3.setDescription("category3");
        productDto3.setCategory(categoryDto3);
        
        

        return List.of(productDto, productDto2, productDto3);
    }
    
    @Test
    void should_find_by_id(){
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductDto actualProduct = productService.findById(product.getId());
        ProductDto expectedProduct = productDto;
        
        assertThat(expectedProduct).usingRecursiveComparison().isEqualTo(actualProduct);
        verify(productRepository).findById(product.getId());
    }
    
    @Test
    void should_not_find_by_id(){
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        Throwable throwable = catchThrowable(() -> productService.findById(product.getId()));
        assertInstanceOf(ProductNotFoundException.class, throwable);
        assertEquals("No product found with id: " + product.getId(), throwable.getMessage());
        verify(productRepository).findById(product.getId());
    }
    
    @Test
    void shouldListAllProducts(){
        mockAuthentication();
        when(productRepository.findProductsByCategory_Company_Id(anyLong())).thenReturn(getMultipleProducts());
        
        List<ProductDto> actualProducts = productService.listAllProducts();
        List<ProductDto> expectedProducts = getMultipleProductDtos();
        
        assertThat(actualProducts).usingRecursiveComparison().isEqualTo(expectedProducts);
        verify(productRepository).findProductsByCategory_Company_Id(1L);

    }
    
    @Test
    void should_delete_product(){
        mockAuthentication();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        productService.deleteProduct(product.getId());
        assertTrue(product.getIsDeleted());
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
