package com.cydeo.service;

import com.cydeo.dto.RoleDto;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.RoleRepository;
import com.cydeo.service.impl.RoleServiceImpl;
import org.hibernate.annotations.Where;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private RoleServiceImpl roleService;
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    
    Role role;
    RoleDto roleDto;
    
    @BeforeEach
    void setUp(){
        role = new Role();
        role.setId(2L);
        role.setDescription("Admin");

        roleDto = new RoleDto();
        roleDto.setId(2L);
        roleDto.setDescription("Admin");
        
    }
    
    private List<Role> getMultipleRoles(){
        Role role1 = new Role();
        role1.setId(1L);
        role1.setDescription("Root User");
        Role role2 = new Role();
        role2.setId(3L);
        role2.setDescription("Manager");
        
        return List.of(role, role1, role2);
        
    }
    
    private List<RoleDto> getMultipleRoleDtos(){
        RoleDto roleDto1 = new RoleDto();
        roleDto1.setId(1L);
        roleDto1.setDescription("Root User");
        
        RoleDto roleDto2 = new RoleDto();
        roleDto2.setId(3L);
        roleDto2.setDescription("Manager");
        
        return List.of(roleDto, roleDto1, roleDto2);
    }
    
    @Test
    void should_list_all_roles(){
        mockAuthentication();
        when(roleRepository.findAll()).thenReturn(getMultipleRoles());

        List<RoleDto> actualRoles = roleService.listAllRoles();
        List<RoleDto> expectedRoles = getMultipleRoleDtos().stream().filter(roleDto -> !roleDto.getId().equals(1L)).collect(Collectors.toList());
        
        assertThat(expectedRoles).usingRecursiveComparison().isEqualTo(actualRoles);
        
        verify(roleRepository).findAll();
    }
    
    private void mockAuthentication(){
        lenient().when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("username");

        SecurityContextHolder.setContext(securityContext);
        
    }
    
}
