package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.ClientVendorRepository;
import com.cydeo.service.impl.ClientVendorServiceImpl;
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
public class ClientVendorServiceTest {
    
    @Mock
    private ClientVendorRepository clientVendorRepository;
    @Mock
    private UserService userService;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private ClientVendorServiceImpl clientVendorService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    
    private ClientVendor clientVendor;
    private ClientVendorDto clientVendorDto;
    
    @BeforeEach
    void setUp(){
        clientVendor = new ClientVendor();
        clientVendor.setId(1L);
        clientVendor.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor.setClientVendorName("clientvendor");

        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setTitle("company");
        clientVendor.setCompany(company);

        
        clientVendorDto = new ClientVendorDto();
        clientVendorDto.setId(1L);
        clientVendorDto.setClientVendorType(ClientVendorType.CLIENT);
        clientVendorDto.setClientVendorName("clientvendor");

        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto.setTitle("company");
        clientVendorDto.setCompanyDto(companyDto);
        
    }
    
    private List<ClientVendor> getMultipleClientVendors(){
        ClientVendor clientVendor1 = new ClientVendor();
        clientVendor1.setId(2L);
        clientVendor1.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor1.setClientVendorName("clientvendor");

        Company company1 = new Company();
        company1.setId(2L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company");
        clientVendor1.setCompany(company1);

        ClientVendor clientVendor2 = new ClientVendor();
        clientVendor2.setId(3L);
        clientVendor2.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor2.setClientVendorName("clientvendor");

        Company company2 = new Company();
        company2.setId(3L);
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setTitle("company");
        clientVendor2.setCompany(company2);
        
        return List.of(clientVendor, clientVendor1, clientVendor2);
    }

    private List<ClientVendorDto> getMultipleClientVendorDtos(){
        ClientVendorDto clientVendor1 = new ClientVendorDto();
        clientVendor1.setId(2L);
        clientVendor1.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor1.setClientVendorName("clientvendor");

        CompanyDto company1 = new CompanyDto();
        company1.setId(2L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company");
        clientVendor1.setCompanyDto(company1);

        ClientVendorDto clientVendor2 = new ClientVendorDto();
        clientVendor2.setId(3L);
        clientVendor2.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor2.setClientVendorName("clientvendor");

        CompanyDto company2 = new CompanyDto();
        company2.setId(3L);
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company2.setTitle("company");
        clientVendor2.setCompanyDto(company2);

        return List.of(clientVendorDto, clientVendor1, clientVendor2);
    }
}
