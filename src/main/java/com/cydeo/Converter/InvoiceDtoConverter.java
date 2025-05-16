package com.cydeo.Converter;

import com.cydeo.dto.InvoiceDto;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class InvoiceDtoConverter implements Converter<String, InvoiceDto> {

    @Override
    public InvoiceDto convert(String source) {
        return null;
    }
}
