package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CompanyRepository;
import com.cydeo.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    @Override
    public List<CompanyDto> listAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::convertToCompanyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDto findById(Long companyId) {
        return convertToCompanyDTO(findCompanyById(companyId));
    }

    @Override
    public void deleteCompany(Long companyId) {
       softDeleteCompany(findCompanyById(companyId));
    }

    @Override
    public void saveCompany(CompanyDto companyDto) {
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyRepository.save(convertToEntity(companyDto));
    }

    @Override
    public void updateCompany(CompanyDto companyDto) {
        companyDto.setCompanyStatus(findCompanyById(companyDto.getId()).getCompanyStatus());
        companyRepository.save(convertToEntity(companyDto));

    }

    @Override
    public void activateCompany(Long companyId) {
        Company company = findCompanyById(companyId);
        if (!company.getCompanyStatus().equals(CompanyStatus.ACTIVE)){
            company.setCompanyStatus(CompanyStatus.ACTIVE);
        }
        companyRepository.save(company);
    }

    @Override
    public void deactivateCompany(Long companyId) {
        Company company = findCompanyById(companyId);
        if (!company.getCompanyStatus().equals(CompanyStatus.PASSIVE)){
            company.setCompanyStatus(CompanyStatus.PASSIVE);
        }
        companyRepository.save(company);
    }

    private CompanyDto convertToCompanyDTO(Company company){
        return mapperUtil.convert(company, new CompanyDto());
    }
    
    private Company convertToEntity(CompanyDto companyDto){
        return mapperUtil.convert(companyDto, new Company());
    }
    
    
    private Company findCompanyById(Long companyId){
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("No company found with id: " + companyId));
    }
    
    private void softDeleteCompany(Company company){
        company.setIsDeleted(true);
        companyRepository.save(company);
    }
}
