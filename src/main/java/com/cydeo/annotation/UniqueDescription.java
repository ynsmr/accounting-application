package com.cydeo.annotation;

import com.cydeo.validator.UniqueDescriptionValidator;
import org.springframework.validation.annotation.Validated;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueDescriptionValidator.class)
public @interface UniqueDescription {
    String message() default "Description already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
