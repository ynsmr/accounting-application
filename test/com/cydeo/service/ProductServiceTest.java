package com.cydeo.service;

import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.ProductRepository;
import com.cydeo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
    
    @Test
    void should_find_by_id(){
        
    }
}
