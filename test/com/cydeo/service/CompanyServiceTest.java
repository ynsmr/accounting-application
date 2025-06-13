package com.cydeo.service;

import com.cydeo.client.CountryClient;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CompanyRepository;
import com.cydeo.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {
    
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private UserService userService;
    @Mock
    private CountryClient countryClient;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private CompanyServiceImpl companyService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    private Company company;
    private CompanyDto companyDto;
    
    @BeforeEach
    void setUp(){
        //Company
        company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setTitle("company");

        companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto.setTitle("company");
    }
    
    private List<Company> getMultipleCompanies(){
        Company company1 = new Company();
        company1.setId(2L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company1");

        Company company2 = new Company();
        company2.setId(3L);
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setTitle("company1");
        
        return List.of(company, company1, company2);
    }

    private List<CompanyDto> getMultipleCompanieDtos(){
        CompanyDto company1 = new CompanyDto();
        company1.setId(2L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company1");

        CompanyDto company2 = new CompanyDto();
        company2.setId(3L);
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setTitle("company1");

        return List.of(companyDto, company1, company2);
    }
    
    
}
