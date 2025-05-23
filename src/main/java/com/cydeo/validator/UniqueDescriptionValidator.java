package com.cydeo.validator;

import com.cydeo.annotation.UniqueDescription;

import com.cydeo.respository.CategoryRepository;
import com.cydeo.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@Component
@AllArgsConstructor
public class UniqueDescriptionValidator implements ConstraintValidator<UniqueDescription, String> {
    
    private final CategoryRepository categoryRepository;
    private final CompanyService companyService;
    
    @Override
    public boolean isValid(String description, ConstraintValidatorContext context) {
        if (description == null) {
            return true; // Let @NotBlank handle null values
        }
        return !categoryRepository.existsByDescriptionAndCompany_Id(description, companyService.retrieveCurrentCompany());
    }
}
