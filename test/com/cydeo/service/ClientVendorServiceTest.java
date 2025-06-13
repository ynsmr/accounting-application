package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.ClientVendorRepository;
import com.cydeo.service.impl.ClientVendorServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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
        clientVendorDto.setCompany(companyDto);
        
    }
    
    private List<ClientVendor> getMultipleClientVendors(){
        ClientVendor clientVendor1 = new ClientVendor();
        clientVendor1.setId(2L);
        clientVendor1.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor1.setClientVendorName("clientvendor");

        Company company1 = new Company();
        company1.setId(1L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company");
        clientVendor1.setCompany(company1);

        ClientVendor clientVendor2 = new ClientVendor();
        clientVendor2.setId(3L);
        clientVendor2.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor2.setClientVendorName("clientvendor");
        
        clientVendor2.setCompany(company1);
        
        return List.of(clientVendor, clientVendor1, clientVendor2);
    }

    private List<ClientVendorDto> getMultipleClientVendorDtos(){
        ClientVendorDto clientVendor1 = new ClientVendorDto();
        clientVendor1.setId(2L);
        clientVendor1.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor1.setClientVendorName("clientvendor");

        CompanyDto company1 = new CompanyDto();
        company1.setId(1L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setTitle("company");
        clientVendor1.setCompany(company1);

        ClientVendorDto clientVendor2 = new ClientVendorDto();
        clientVendor2.setId(3L);
        clientVendor2.setClientVendorType(ClientVendorType.CLIENT);
        clientVendor2.setClientVendorName("clientvendor");
        
        clientVendor2.setCompany(company1);

        return List.of(clientVendorDto, clientVendor1, clientVendor2);
    }
    
    @Test
    void should_list_all(){
        mockAuthentication();
        when(clientVendorRepository.findAll()).thenReturn(getMultipleClientVendors());

        List<ClientVendorDto> actualClientVendors = clientVendorService.listAll();
        List<ClientVendorDto> expectedClientVendors = getMultipleClientVendorDtos();
        
        assertThat(actualClientVendors).usingRecursiveComparison().isEqualTo(expectedClientVendors);
        verify(clientVendorRepository).findAll();

    }
    
    @Test
    void should_list_all_by_type(){
        mockAuthentication();
        when(clientVendorRepository.findAllByClientVendorType(any())).thenReturn(getMultipleClientVendors());

        List<ClientVendorDto> actualClientVendors = clientVendorService.listAllByType(ClientVendorType.CLIENT);
        List<ClientVendorDto> expectedClientVendors = getMultipleClientVendorDtos();
        
        assertThat(actualClientVendors).usingRecursiveComparison().isEqualTo(expectedClientVendors);
        verify(clientVendorRepository).findAllByClientVendorType(ClientVendorType.CLIENT);
    }
    
    @Test
    void should_save_client_vendor(){
        when(clientVendorRepository.save(any())).thenReturn(clientVendor);
        
        clientVendorService.saveClientVendor(clientVendorDto);
        
        verify(clientVendorRepository).save(clientVendor);
    }

    @Test
    void should_update_client_vendor(){
        when(clientVendorRepository.save(any())).thenReturn(clientVendor);

        clientVendorService.updateClientVendor(clientVendorDto);

        verify(clientVendorRepository).save(clientVendor);
    }
    
    @Test
    void should_delete_client_vendor(){
        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.of(clientVendor));
        
        clientVendorService.deleteClientVendor(clientVendor.getId());
        
        assertTrue(clientVendor.getIsDeleted());
        verify(clientVendorRepository).save(clientVendor);
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
