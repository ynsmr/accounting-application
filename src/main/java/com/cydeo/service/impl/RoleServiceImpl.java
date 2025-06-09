package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.entity.Role;
import com.cydeo.exception.RoleNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;
    
    @Override
    public List<RoleDto> listAllRoles() {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return roleRepository.findAll().stream()
                .filter(role -> !role.getId().equals(1L))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto findById(Long roleId) {
        return convertToDTO(findRoleById(roleId));
    }

    private RoleDto convertToDTO(Role role){
        return mapperUtil.convert(role, new RoleDto());
    }
    
    private Role findRoleById(Long roleId){
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("No role found with id: " + roleId));
    }
}
