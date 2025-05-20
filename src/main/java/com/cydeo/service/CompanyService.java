package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;

public interface CompanyService {
    
    CompanyDto findCompanyByUser(Long userId);
    List<CompanyDto> listAllCompanies();
    CompanyDto findById(Long companyId);
    void delete(Long companyId);
    void save(CompanyDto companyDto);
    void update(CompanyDto companyDto);
    void activate(Long companyId);
    void deactivate(Long companyId);
    Long retrieveCurrentCompany();
    
    
}
