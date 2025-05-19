package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.UserRepository;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    

    @Override
    public UserDto findByUsername(String username) {
        Optional<User> userRetrieved = userRepository.findByUsername(username);
        if (userRetrieved.isEmpty()){
            throw new NoSuchElementException("No such user found on DB");
        }
        return mapperUtil.convert(userRetrieved, new UserDto());
    }

    @Override
    public List<UserDto> listAllUsers() {
        userIsOnlyAdmin(getLoggedInUser());
        return userRepository.findAll().stream()
                .filter(User::isAccountNonLocked)
                .filter(user -> !user.getCompany().getId().equals(1L))
                .filter(user -> user.getCompany().getId().equals(getLoggedInUser().getCompany().getId()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long userId) {
        return convertToDTO(findUserById(userId));
    }

    @Override
    public void deleteById(Long userId) {
        User user = findUserById(userId);
        softDeleteUser(user);
    }

    @Override
    public void saveUser(UserDto userDto) {
        findUserById(userDto.getId()).setAccountNonLocked(true);
        userRepository.save(convertToEntity(userDto));
    }

    @Override
    public void updateUser(UserDto userDto) {
        userRepository.save(convertToEntity(userDto));
        
    }

    @Override
    public List<User> findUsersByCompanyId(Long companyId) {
        return userRepository.findUsersByCompany_Id(companyId);
    }

    @Override
    public boolean userIsOnlyAdmin(UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        return roles.contains("Admin") && roles.size() == 1;
    }

    @Override
    public boolean notARootUser() {
        return !getLoggedInUser().getRole().getId().equals(1L);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " does not exist"));
    }
    
    private void softDeleteUser(User user) {
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    private UserDto convertToDTO(User user){
        return mapperUtil.convert(user, new UserDto());
    }
    
    private User convertToEntity(UserDto userDto){
        return mapperUtil.convert(userDto, new User());
    }
    
    @Override
    public UserDto getLoggedInUser(){
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(loggedInUser);
    }
    
}