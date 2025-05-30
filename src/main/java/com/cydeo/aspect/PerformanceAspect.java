package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
public class PerformanceAspect {

    
    @Pointcut("@annotation(com.cydeo.annotation.ExecutionTime)")
    private void anyExecutionTimeOperation(){};
    
    @Around("anyExecutionTimeOperation()")
    public Object anyExecutionTimeOperationAdvice(ProceedingJoinPoint proceedingJoinPoint){
        long beforeTime = System.currentTimeMillis();
        Object result = null;
        
        log.info("Execution will start " + proceedingJoinPoint.getSignature().toShortString());

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        long afterTime = System.currentTimeMillis();
        log.info("Time taken to execute : {} ms - Method : {} - Parameters : {}", (afterTime-beforeTime), proceedingJoinPoint.getSignature().toShortString(), proceedingJoinPoint.getArgs());
        
        return result;
        
    }
}
