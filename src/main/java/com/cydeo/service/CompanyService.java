package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;

public interface CompanyService {
    
    CompanyDto findCompanyByUser(Long userId);
    List<CompanyDto> listAllCompanies();
    CompanyDto findById(Long companyId);
    void deleteCompany(Long companyId);
    void saveCompany(CompanyDto companyDto);
    void updateCompany(CompanyDto companyDto);
    void activateCompany(Long companyId);
    void deactivateCompany(Long companyId);
    
    
}
