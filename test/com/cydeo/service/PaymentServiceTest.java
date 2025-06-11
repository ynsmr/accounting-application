package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Payment;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.enums.Month;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.PaymentRepository;
import com.cydeo.service.impl.PaymentServiceImpl;
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

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    
    Payment payment;
    PaymentDto paymentDto;
    
    @BeforeEach
    void setUp(){
        //Payment
        payment = new Payment();
        payment.setId(1L);
        payment.setPaid(false);
        payment.setYear(2025);
        payment.setMonth(Month.APRIL);
        payment.setAmount(BigDecimal.valueOf(5655));

        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        payment.setCompany(company);

        //PaymentDto
        paymentDto = new PaymentDto();
        paymentDto.setId(1L);
        paymentDto.setPaid(false);
        paymentDto.setYear(2025);
        paymentDto.setMonth(Month.APRIL);
        paymentDto.setAmount(BigDecimal.valueOf(5655));

        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        paymentDto.setCompany(companyDto);
        
    }
    
    private List<Payment> getMultiplePayments(){
        Payment payment1 = new Payment();
        payment1.setId(2L);
        payment1.setPaid(false);
        payment1.setYear(2025);
        payment1.setMonth(Month.MARCH);
        payment1.setAmount(BigDecimal.valueOf(5655));

        Company company1 = new Company();
        company1.setId(1L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        payment1.setCompany(company1);

        Payment payment2 = new Payment();
        payment2.setId(3L);
        payment2.setPaid(false);
        payment2.setYear(2025);
        payment2.setMonth(Month.MARCH);
        payment2.setAmount(BigDecimal.valueOf(5655));
        
        payment2.setCompany(company1);
        
        return List.of(payment, payment1, payment2);
        
    }

    private List<PaymentDto> getMultiplePaymentDtos(){
        PaymentDto payment1 = new PaymentDto();
        payment1.setId(2L);
        payment1.setPaid(false);
        payment1.setYear(2025);
        payment1.setMonth(Month.MARCH);
        payment1.setAmount(BigDecimal.valueOf(5655));

        CompanyDto company1 = new CompanyDto();
        company1.setId(1L);
        company1.setCompanyStatus(CompanyStatus.ACTIVE);
        payment1.setCompany(company1);

        PaymentDto payment2 = new PaymentDto();
        payment2.setId(3L);
        payment2.setPaid(false);
        payment2.setYear(2025);
        payment2.setMonth(Month.MARCH);
        payment2.setAmount(BigDecimal.valueOf(5655));
        
        payment2.setCompany(company1);

        return List.of(paymentDto, payment1, payment2);

    }
    
    
    
    
    
    @Test
    void should_list_all_payments(){
        mockAuthentication();
        when(paymentRepository.findAll()).thenReturn(getMultiplePayments());

        List<PaymentDto> actualPayments = paymentService.listAllPayments();
        List<PaymentDto> expectedPayments = getMultiplePaymentDtos();
        
        assertThat(actualPayments).usingRecursiveComparison().isEqualTo(expectedPayments);
        
        verify(paymentRepository).findAll();

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
        user.setCompany(company);

        lenient().when(userService.getLoggedInUser()).thenReturn(user);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContextHolder.setContext(securityContext);

    }
    
    
}
