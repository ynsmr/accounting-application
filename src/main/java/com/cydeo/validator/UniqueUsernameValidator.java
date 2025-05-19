package com.cydeo.validator;

import com.cydeo.annotation.UniqueUserName;
import com.cydeo.respository.UserRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUserName, String> {
    
    private final UserRepository userRepository;
    
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // Let @NotBlank handle null values
        }
        return userRepository.findByUsername(username).isEmpty();
        
    }
}
