package com.cydeo.Converter;

import com.cydeo.dto.ProductDto;
import com.cydeo.service.ProductService;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ProductDTOConverter implements Converter<String, ProductDto> {
    
    private final ProductService productService;

    public ProductDTOConverter(@Lazy ProductService productService) {
        this.productService = productService;
    }


    @Override
    public ProductDto convert(String source) {
        return productService.findById(Long.valueOf(source));
    }
}
