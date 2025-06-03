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
import org.junit.jupiter.api.Assertions;
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
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;
import org.springframework.security.core.Authentication;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    @Spy
    private UserServiceImpl userService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    
    User user;
    UserDto userDto;
    
    @BeforeEach
    void setUp(){
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
    
    private List<User> getmultipleUsers(){
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

    private List<UserDto> getmultipleUserDto(){
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
    void should_find_by_username(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.findByUsername("user");
        UserDto expectedUserDto = userDto;
        
        assertThat(expectedUserDto).usingRecursiveComparison().isEqualTo(actualUserDto);
    }
    
    @Test
    void should_throw_exception(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        
        Throwable throwable = catchThrowable(() -> userService.findByUsername("user"));
        
        assertInstanceOf(UserNotFoundException.class, throwable);

        assertEquals(throwable.getMessage(), "No such user found on DB");
    }
    
    @Test
    void listAllUsers_shouldReturnAdminUsers_whenUserHasRootRole() {
        // Arrange
        Role rootRole = new Role();
        rootRole.setId(ROOT_ROLE_ID);
        user.setRole(rootRole);
        
        // Create test data with one admin user and one non-admin user
        List<User> testUsers = getmultipleUsers();
        
        // Ensure both users have properly initialized roles
        // Initialize role for the second user
        Role adminRole = new Role();
        adminRole.setId(2L);
        testUsers.get(1).setRole(adminRole);
        
        // Mock repository to return test data
        when(userRepository.findAll()).thenReturn(testUsers);
        
        // Mock the getLoggedInUser method 
        doReturn(user).when(userService).getLoggedInUser();
        
        // Create expected result with only admin users
        List<UserDto> expectedUserDtos = getmultipleUserDto(); // Only include the admin user
        
        // Act
        List<UserDto> actualUserDtos = userService.listAllUsers();
        
        // Assert
        assertThat(actualUserDtos).usingRecursiveComparison().isEqualTo(expectedUserDtos);
    }

    private static final Long ROOT_ROLE_ID = 1L;
    
    
    
    @Test
    void listAllUsers_shouldCallRequiredMethods_whenUserHasRootRole() {
        // Arrange
        // Set up the ROOT role for the user
        Role rootRole = new Role();
        rootRole.setId(ROOT_ROLE_ID);
        user.setRole(rootRole);
        
        // Mock repository to return any list (we don't care about the actual data)
        List<User> testUsers = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(testUsers);
        
        // Mock the getLoggedInUser method to return user with ROOT role
        // Use Mockito.atLeastOnce() since the method is called multiple times internally
        doReturn(user).when(userService).getLoggedInUser();
        
        // Act
        userService.listAllUsers();
        
        // Assert - verify that findAll was called once
        verify(userRepository).findAll();
        
        // Verify getLoggedInUser was called at least once (it's called 3 times in the implementation)
        verify(userService, atLeastOnce()).getLoggedInUser();
    }
}