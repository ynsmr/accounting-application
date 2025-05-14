package com.cydeo.Converter;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import lombok.AllArgsConstructor;
import org.modelmapper.spi.MappingContext;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class CompanyDtoConverter implements Converter<Long, CompanyDto> {
    
    private final CompanyService companyService;

    public CompanyDtoConverter(@Lazy CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public CompanyDto convert(Long userId) {
        return companyService.findCompanyByUser(userId);
    }
    
    
}
