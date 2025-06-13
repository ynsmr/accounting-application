package com.cydeo.service;

import com.cydeo.client.CountryClient;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.countries.Country;
import com.cydeo.dto.countries.Name;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CompanyNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.CompanyRepository;
import com.cydeo.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

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
        company.setId(2L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setTitle("company");

        companyDto = new CompanyDto();
        companyDto.setId(2L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto.setTitle("company");
    }
    
    private List<Company> getMultipleCompanies(){
        Company company1 = new Company();
        company1.setId(3L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company1");

        Company company2 = new Company();
        company2.setId(4L);
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setTitle("company2");
        
        return List.of(company, company1, company2);
    }

    private List<CompanyDto> getMultipleCompanieDtos(){
        CompanyDto company1 = new CompanyDto();
        company1.setId(3L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company1");

        CompanyDto company2 = new CompanyDto();
        company2.setId(4L);
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setTitle("company2");

        return List.of(companyDto, company1, company2);
    }
    
    private List<Country> getMultipleCountries(){
        Country country = new Country();
        Name name = new Name();
        name.setCommon("USA");
        name.setOfficial("USA");
        country.setName(name);

        Country country1 = new Country();
        Name name1 = new Name();
        name1.setCommon("UK");
        name1.setOfficial("UK");
        country1.setName(name1);

        Country country2 = new Country();
        Name name2 = new Name();
        name2.setCommon("UAE");
        name2.setOfficial("UAE");
        country2.setName(name2);
        
        return List.of(country, country1, country2);
    }
    
    @Test
    void should_find_company_by_user(){
        when(companyRepository.findCompanyByLoggedInUser(anyLong())).thenReturn(Optional.of(company));

        CompanyDto actualCompany = companyService.findCompanyByUser(1L);
        CompanyDto expectedCompany = companyDto;
        
        assertThat(actualCompany).usingRecursiveComparison().isEqualTo(expectedCompany);
        verify(companyRepository).findCompanyByLoggedInUser(1L);

    }
    
    @Test
    void should_not_find_company_by_user(){
        when(companyRepository.findCompanyByLoggedInUser(anyLong())).thenReturn(Optional.empty());
        
        Throwable throwable = catchThrowable(() -> companyService.findCompanyByUser(1L));
        
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        assertEquals("No associated company found", throwable.getMessage());
        
        verify(companyRepository).findCompanyByLoggedInUser(1L);
    }
    
    @Test
    void should_list_all_companies(){
        mockAuthentication();
        when(companyRepository.findAll()).thenReturn(getMultipleCompanies());
        when(userService.notARootUser(any())).thenReturn(false);
        
        List<CompanyDto> actualCompanies = companyService.listAllCompanies();
        List<CompanyDto> expectedCompanies = getMultipleCompanieDtos();
        
        assertThat(actualCompanies).usingRecursiveComparison().isEqualTo(expectedCompanies);
        verify(companyRepository).findAll();
    }
    
    @Test
    void should_find_by_id(){
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));

        CompanyDto actualCompany = companyService.findById(company.getId());
        CompanyDto expectedCompany = companyDto;
        
        assertThat(actualCompany).usingRecursiveComparison().isEqualTo(expectedCompany);
        verify(companyRepository).findById(company.getId());
    }
    
    @Test
    void should_not_find_by_id(){
        when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        Throwable throwable = catchThrowable(() -> companyService.findById(company.getId()));
        
        assertInstanceOf(CompanyNotFoundException.class, throwable);
        
        assertEquals("No company found with id: " + company.getId(), throwable.getMessage());
        verify(companyRepository).findById(company.getId());
    }
    
    @Test
    void should_delete(){
        when(companyRepository.save(any())).thenReturn(company);
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        
        companyService.delete(company.getId());
        
        assertTrue(company.getIsDeleted());
        verify(companyRepository).save(company);
    }
    
    @Test
    void should_save(){
        companyDto.setCompanyStatus(CompanyStatus.PASSIVE);
        when(companyRepository.save(any())).thenReturn(company);
        
        companyService.save(companyDto);
        assertEquals(CompanyStatus.ACTIVE, companyDto.getCompanyStatus());
        
        verify(companyRepository).save(company);
    }
    
    @Test
    void should_update(){
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        when(companyRepository.save(any())).thenReturn(company);
        
        companyService.update(companyDto);
        
        verify(companyRepository).save(company);
    }
    
    @Test
    void should_activate(){
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        
        companyService.activate(company.getId());
        
        verify(userService).findUsersByCompanyId(company.getId());
        verify(companyRepository).save(company);
    }

    @Test
    void should_deactivate(){
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));

        companyService.deactivate(company.getId());

        verify(userService).findUsersByCompanyId(company.getId());
        verify(companyRepository).save(company);
    }
    
    @Test
    void should_retrieve_current_company(){
        mockAuthentication();

        Long actualCompanyId = companyService.retrieveCurrentCompany();
        
        assertEquals(1L, actualCompanyId);
        verify(userService).getLoggedInUser();
    }
    
    @Test
    void should_list_country_names(){
        List<String> countryNames = getMultipleCountries().stream()
                .map(Country::getName)
                .map(Name::getOfficial)
                .collect(Collectors.toList());
        
        when(countryClient.getCountries(anyString())).thenReturn(getMultipleCountries());

        List<String> actualCountryNames = companyService.getOfficialCountryNames();
        
        assertThat(actualCountryNames).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(countryNames);
        verify(countryClient).getCountries("name");
    }
    
    

    private void mockAuthentication(){
        User user = new User();
        user.setId(1L);
        user.setFirstname("Mike");
        user.setLastname("Tyson");
        user.setUsername("miketyson");
        user.setAccountNonLocked(true);

        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setInsertDateTime(LocalDateTime.of(2012, 12, 12, 0, 0, 0));
        user.setCompany(company);

        lenient().when(userService.getLoggedInUser()).thenReturn(user);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

    }
    
    
    
}
