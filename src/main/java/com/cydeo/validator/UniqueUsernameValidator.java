package com.cydeo.validator;

import com.cydeo.annotation.UniqueUserName;
import com.cydeo.respository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@AllArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUserName, String> {
    
    private final UserRepository userRepository;
    
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // Let @NotBlank handle null values
        }
        return !userRepository.existsByUsername(username);
        
    }
}
