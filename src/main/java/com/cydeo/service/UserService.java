package com.cydeo.service;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;

import java.util.List;

public interface UserService {
    
    UserDto findByUsername(String username);
    
    List<UserDto> listAllUsers();
    
    UserDto findById(Long userId);
    
    void deleteById(Long userId);
    
    void saveUser(UserDto userDto);
    
    void updateUser(UserDto userDto);
    
    List<User> findUsersByCompanyId(Long companyId);
    
    boolean userIsOnlyAdmin(User user);
    
    boolean notARootUser();
    
    User getLoggedInUser();
    
}
