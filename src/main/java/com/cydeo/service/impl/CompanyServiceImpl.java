package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CompanyRepository;
import com.cydeo.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    
    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;
    
    @Override
    public CompanyDto findCompanyByUser(Long userId) {
        return convertToCompanyDTO(companyRepository
                .findCompanyByLoggedInUser(userId)
                .orElseThrow(() -> new NoSuchElementException("No associated company found")));
    }
    
    private CompanyDto convertToCompanyDTO(Company company){
        return mapperUtil.convert(company, new CompanyDto());
    }
    
}
