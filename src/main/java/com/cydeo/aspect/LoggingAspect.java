package com.cydeo.aspect;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Aspect
@Configuration
@AllArgsConstructor
public class LoggingAspect {
    
    private final CompanyService companyService;

    private String getUserName(){
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return username;
    }
    
    @Pointcut("execution(* com.cydeo.controller.CompanyController.activateCompany(..)) || execution(* com.cydeo.controller.CompanyController.deactivateCompany(..))")
    private void anyCompanyActivateDeactivateOperation(){};
    
    @AfterReturning(pointcut = "anyCompanyActivateDeactivateOperation()", returning = "results")
    public void anyAfterCompanyActivationAdvice(JoinPoint joinPoint, Object results){
        String username = getUserName();
        String company = companyService.findById((Long) joinPoint.getArgs()[0]).getTitle();
        log.info("AfterReturning  -> User : {} - Company : {} - Method : {} - Results: {}", username, company, joinPoint.getSignature().toShortString(), results.toString());

    }
    
}
