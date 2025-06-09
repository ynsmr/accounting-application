package com.cydeo.service;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.enums.ProductUnit;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.ProductRepository;
import com.cydeo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InvoiceProductService invoiceProductService;
    @Mock
    private UserService userService;
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
        
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setProductUnit(ProductUnit.PCS);
        productDto.setName("product");
        productDto.setLowLimitAlert(15);
        productDto.setQuantityInStock(24);
    }
    
    private List<Product> getMultipleProducts(){
        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductUnit(ProductUnit.PCS);
        product2.setName("product2");
        product2.setLowLimitAlert(25);
        product2.setQuantityInStock(54);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setProductUnit(ProductUnit.PCS);
        product3.setName("product3");
        product3.setLowLimitAlert(25);
        product3.setQuantityInStock(54);
        
        return List.of(product, product2, product3);
    }

    private List<ProductDto> getMultipleProductDtos(){
        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        productDto2.setProductUnit(ProductUnit.PCS);
        productDto2.setName("productDto2");
        productDto2.setLowLimitAlert(25);
        productDto2.setQuantityInStock(54);

        ProductDto productDto3 = new ProductDto();
        productDto3.setId(3L);
        productDto3.setProductUnit(ProductUnit.PCS);
        productDto3.setName("productDto3");
        productDto3.setLowLimitAlert(25);
        productDto3.setQuantityInStock(54);

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
}
