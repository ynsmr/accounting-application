package com.cydeo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        // Add the exception message
        model.addAttribute("message", ex.getMessage());

        // Optionally, add the stack trace or cause if needed
        model.addAttribute("stackTrace", ex.getStackTrace());
        return "error"; // error.html
    }
}