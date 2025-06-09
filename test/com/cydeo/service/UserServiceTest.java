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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import org.springframework.security.core.Authentication;

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
        user.setAccountNonLocked(true);

        Company company = new Company();
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setId(1L);
        user.setCompany(company);

        Role role = new Role();
        role.setId(2L);
        user.setRole(role);
        
        //UserDto
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("username");
        userDto.setFirstname("firstname");
        userDto.setLastname("lastname");
        userDto.setPhone("123456789");
        
        CompanyDto companyDto = new CompanyDto();
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto.setId(1L);
        userDto.setCompany(companyDto);
      

        RoleDto roleDto = new RoleDto();
        roleDto.setId(2L);
        userDto.setRole(roleDto);
        
    }

    private List<User> getmultipleUsers() {
        User user1 = new User();
        user1.setId(2L);
        user1.setUsername("Johnny");
        user1.setFirstname("John");
        user1.setLastname("Wick");
        user1.setAccountNonLocked(true);  // Ensure the user is active

        Company company = new Company();
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        company.setId(1L);
        user1.setCompany(company);
        
        Role role = new Role();
        role.setId(2L);  // Matches ADMIN_ROLE_ID in the method fetchUsersFilteredByRole
        user1.setRole(role);
        

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

        assertEquals("No such user found on DB", throwable.getMessage());
    }

    @Test
    void should_return_admin_users_only() {
        // Arrange: Root user role assignment
        Role role = new Role();
        role.setId(1L); // Root user role
        user.setRole(role);
        

        // Mock SecurityContext and Authentication functionality
        mockAuthentication();
        
        // Mock the repository call for all users
        when(userRepository.findAll()).thenReturn(getmultipleUsers());

        // Act: Call the method under test
        List<UserDto> actualUsers = userService.listAllUsers();
        
        assertEquals(1, actualUsers.size());
        
        // Verify method calls
        verify(userRepository).findByUsername("username");
        verify(userRepository).findAll();
    }
    
    @Test
    void should_return_allUsers(){
        // Arrange: Root user role assignment
        Role role = new Role();
        role.setId(2L); // Root user role
        user.setRole(role);


        // Mock SecurityContext and Authentication functionality
        mockAuthentication();

        // Mock the repository call for all users
        when(userRepository.findAll()).thenReturn(getmultipleUsers());

        // Act: Call the method under test
        List<UserDto> actualUsers = userService.listAllUsers();

        assertEquals(2, actualUsers.size());

        // Verify method calls
        verify(userRepository).findByUsername("username");
        verify(userRepository).findAll();
    }
    
    @Test
    void should_find_by_id(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto actualUser = userService.findById(1L);
        UserDto expectedUser = userDto;
        
        assertThat(expectedUser).usingRecursiveComparison().isEqualTo(actualUser);

    }
    
    @Test
    void should_delete_by_id(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        
        userService.deleteById(1L);
        
        assertEquals(true, user.getIsDeleted());
        
    }
    
    @Test
    void should_save_user(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        userService.saveUser(userDto);

        assertTrue(user.isAccountNonLocked());
        verify(userRepository).save(user);
        
    }
    
    @Test
    void should_update_user(){
        when(userRepository.save(any())).thenReturn(user);
        
        userService.updateUser(userDto);
        
        verify(userRepository).save(user);
        
    }
    
    @Test
    void should_find_users_by_company_id(){
        when(userRepository.findUsersByCompany_Id(anyLong())).thenReturn(getmultipleUsers());

        List<User> actualUsers = userService.findUsersByCompanyId(1L);
        List<User> expectedUsers = getmultipleUsers();
        
        assertThat(expectedUsers).usingRecursiveComparison().isEqualTo(actualUsers);
        verify(userRepository).findUsersByCompany_Id(1L);
    }
    
    @Test
    void should_check_admin(){
        mockAuthentication();
        lenient().when(userRepository.findAll()).thenReturn(getmultipleUsers());

        boolean actualBoolean = userService.userIsOnlyAdmin(user);
        
        assertTrue(actualBoolean);

    }
    
    @Test
    void should_not_be_root_user(){
        boolean b = userService.notARootUser(user);
        assertTrue(b);
    }
    
   private void mockAuthentication() {
       // Mock SecurityContext to return the mocked Authentication object
       lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

       // Mock Authentication details (username and authorities)
       lenient().when(authentication.getName()).thenReturn("username");
       lenient().doReturn(List.of(new SimpleGrantedAuthority("Admin"))).when(authentication).getAuthorities();

       // Set the mocked SecurityContext in the SecurityContextHolder
       SecurityContextHolder.setContext(securityContext);

       // Mock the user repository to return a user
       lenient().when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
   }
}