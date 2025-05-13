package com.cydeo.service.impl;

import com.cydeo.entity.User;
import com.cydeo.entity.common.UserPrincipal;
import com.cydeo.respository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityServiceImpl implements SecurityService {
    private final UserRepository userRepository;

    public SecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user= userRepository.findByUsername(username);

        if(user.isEmpty()){
            throw new UsernameNotFoundException("This user does not exists");
        }

        return new UserPrincipal(user.get());    }
}
