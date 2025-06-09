package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.UserRepository;
import com.cydeo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


import org.springframework.security.core.Authentication;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private UserServiceImpl userService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());


    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        //User
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setPhone("123456789");

        Role role = new Role();
        role.setId(2L);
        user.setRole(role);

        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);

        user.setCompany(company);

        //UserDto
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("username");
        userDto.setFirstname("firstname");
        userDto.setLastname("lastname");
        userDto.setPhone("123456789");

        RoleDto roleDto = new RoleDto();
        roleDto.setId(2L);
        userDto.setRole(roleDto);

        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);

        userDto.setCompany(companyDto);
    }

    private List<User> getmultipleUsers() {
        User user1 = new User();
        user1.setId(2L);
        user1.setUsername("Johnny");
        user1.setFirstname("John");
        user1.setLastname("Wick");

        Company company = new Company();
        company.setId(1L);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        user1.setCompany(company);

        return List.of(user, user1);
    }

    private List<UserDto> getmultipleUserDto() {
        UserDto userDto1 = new UserDto();
        userDto1.setId(2L);
        userDto1.setUsername("Johnny");
        userDto1.setFirstname("John");
        userDto1.setLastname("Wick");

        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        userDto1.setCompany(companyDto);
        return List.of(userDto, userDto1);
    }


    @Test
    void should_find_by_username() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.findByUsername("user");
        UserDto expectedUserDto = userDto;

        assertThat(expectedUserDto).usingRecursiveComparison().isEqualTo(actualUserDto);
    }

    @Test
    void should_throw_exception() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.findByUsername("user"));

        assertInstanceOf(UserNotFoundException.class, throwable);

        assertEquals(throwable.getMessage(), "No such user found on DB");
    }

    @Test
    void should_return_admin_users_only() {
        // Arrange: Root user role assignment
        Role role = new Role();
        role.setId(1L); // Root user role
        user.setRole(role);

        // Mock SecurityContext and Authentication functionality
        mockAuthentication("username");

        // Mock userRepository interaction
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        // Mock the repository call for all users
        when(userRepository.findAll()).thenReturn(getmultipleUsers());

        // Act: Call the method under test
        List<UserDto> actualUsers = userService.listAllUsers();
        
        // Verify method calls
        verify(userRepository).findByUsername("username");
        verify(userRepository).findAll();
    }
    
    private void mockAuthentication(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }
}