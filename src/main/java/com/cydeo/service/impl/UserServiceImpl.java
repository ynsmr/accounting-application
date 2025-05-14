package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.UserRepository;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
    }

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
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        return convertToDTO(userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such person with id: "+ id)));
    }


    private UserDto convertToDTO(User user){
        return mapperUtil.convert(user, new UserDto());
    }
}
