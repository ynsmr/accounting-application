package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final MapperUtil mapperUtil;

    @Override
    public CompanyDto findCompanyByUser(Long userId) {
        return convertToCompanyDTO(companyRepository
                .findCompanyByLoggedInUser(userId)
                .orElseThrow(() -> new CompanyNotFoundException("No associated company found")));
    }

    @Override
    public List<CompanyDto> listAllCompanies() {
        boolean isCurrentUserCompanyOnly = userService.notARootUser();
        return getFilteredCompanies(isCurrentUserCompanyOnly);
    }

    @Override
    public CompanyDto findById(Long companyId) {
        return convertToCompanyDTO(findCompanyById(companyId));
    }

    @Override
    public void delete(Long companyId) {
       softDeleteCompany(findCompanyById(companyId));
    }

    @Override
    public void save(CompanyDto companyDto) {
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyRepository.save(convertToEntity(companyDto));
    }

    @Override
    public void update(CompanyDto companyDto) {
        companyDto.setCompanyStatus(findCompanyById(companyDto.getId()).getCompanyStatus());
        companyRepository.save(convertToEntity(companyDto));

    }

    @Override
    public void activate(Long companyId) {
        Company company = findCompanyById(companyId);
        if (!company.getCompanyStatus().equals(CompanyStatus.ACTIVE)){
            company.setCompanyStatus(CompanyStatus.ACTIVE);
        }
        userService.findUsersByCompanyId(companyId).forEach(user -> user.setAccountNonLocked(true));
        companyRepository.save(company);
    }

    @Override
    public void deactivate(Long companyId) {
        Company company = findCompanyById(companyId);
        
        if (!company.getCompanyStatus().equals(CompanyStatus.PASSIVE)){
            company.setCompanyStatus(CompanyStatus.PASSIVE);
        }
        userService.findUsersByCompanyId(companyId).forEach(user -> user.setAccountNonLocked(false));
        companyRepository.save(company);
    }

    @Override
    public Long retrieveCurrentCompany() {
        return userService.getLoggedInUser().getCompany().getId();
    }

    private CompanyDto convertToCompanyDTO(Company company){
        return mapperUtil.convert(company, new CompanyDto());
    }
    
    private Company convertToEntity(CompanyDto companyDto){
        return mapperUtil.convert(companyDto, new Company());
    }
    
    
    private Company findCompanyById(Long companyId){
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("No company found with id: " + companyId));
    }
    
    private void softDeleteCompany(Company company){
        company.setIsDeleted(true);
        companyRepository.save(company);
    }

    private List<CompanyDto> getFilteredCompanies(boolean filterByCurrentUserCompany) {
        List<Company> allCompanies = companyRepository.findAll();
        
        return allCompanies.stream()
                .filter(company -> !company.getId().equals(1L))
                .filter(company -> !filterByCurrentUserCompany || 
                        company.getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .sorted(Comparator.comparing(Company::getCompanyStatus)
                        .thenComparing(Company::getTitle))
                .map(this::convertToCompanyDTO)
                .collect(Collectors.toList());
    }
}