package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.UserRepository;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    
private static final String ADMIN_ROLE = "Admin";
private static final Long ADMIN_ROLE_ID = 2L;


    @Override
    public UserDto findByUsername(String username) {
        Optional<User> userRetrieved = userRepository.findByUsername(username);
        if (userRetrieved.isEmpty()){
            throw new UserNotFoundException("No such user found on DB");
        }
        return mapperUtil.convert(userRetrieved.get(), new UserDto());
    }

@Override
public List<UserDto> listAllUsers() {
    // Cache the logged-in user
    User loggedInUser = getLoggedInUser();

    if (!notARootUser(loggedInUser) && !isAdminUser(loggedInUser)) {
        // Root user who is not admin
        return fetchUsersFilteredByRole(ADMIN_ROLE_ID);
    }

    // Admin or default case
    return fetchUsersByCompany(loggedInUser.getCompany().getId());
}

    private List<UserDto> fetchUsersFilteredByRole(Long roleId) {
        return userRepository.findAll().stream()
                .filter(user -> isActiveUser(user) && roleId.equals(user.getRole().getId()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private List<UserDto> fetchUsersByCompany(Long companyId) {
        return userRepository.findAll().stream()
                .filter(user -> isActiveUser(user) && user.getCompany().getId().equals(companyId))
                .sorted(createUserComparator())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private boolean isActiveUser(User user) {
        return user.isAccountNonLocked();
    }

    private Comparator<User> createUserComparator() {
        return Comparator
                .comparing((User user) -> user.getCompany().getTitle())
                .thenComparing(user -> user.getRole().getDescription());
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
    public boolean userIsOnlyAdmin(User user) {
        // If the user has Admin role in their authentication, they pass the check
        if (hasAdminAuthority()) {
            return true;
        }
        
        // Otherwise, check if they are the only admin in their company
        return isOnlyAdminInCompany();
    }

    private boolean hasAdminAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        return roles.contains(ADMIN_ROLE);
    }

    private boolean isOnlyAdminInCompany() {
        // Count the number of admin users (with role ID 2)
        long adminCount = userRepository.findAll().stream()
                .filter(user -> user.isAccountNonLocked() && user.getRole().getId().equals(ADMIN_ROLE_ID))
                .count();
        
        return adminCount == 1L;
    }

@Override
public boolean notARootUser(User user) {
    return !user.getRole().getId().equals(1L);
}

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " does not exist"));
    }
    
    private void softDeleteUser(User user) {
        user.setIsDeleted(true);
        user.setUsername(user.getUsername()+ "-- VOID");
        userRepository.save(user);
    }

    private UserDto convertToDTO(User user){
        return mapperUtil.convert(user, new UserDto());
    }
    
    private User convertToEntity(UserDto userDto){
        return mapperUtil.convert(userDto, new User());
    }
    
    public boolean isAdminUser(User loggedInUser) { // Accept cached user
    return loggedInUser.getRole().getId().equals(2L);
}
    
    @Override
    public User getLoggedInUser(){
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(loggedInUser)
                .orElseThrow(()-> new UserNotFoundException("Current user not found in DB."));
    }
    
}
