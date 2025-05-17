package com.cydeo.Converter;

import com.cydeo.dto.InvoiceProductDto;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class InvoiceProductDtoConverter implements Converter<String, InvoiceProductDto> {

    @Override
    public InvoiceProductDto convert(String source) {
        return null;
    }
}
