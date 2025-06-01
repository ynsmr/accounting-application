package com.cydeo.controller;

import com.cydeo.service.PaymentService;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Year;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/list")
    public String listPayments(Model model, 
                               @RequestParam(name = "year", 
                                       defaultValue = "#{T(java.time.Year).now().getValue()}", 
                                       required = false) Integer year){
        
        model.addAttribute("payments", paymentService.listPaymentsByYear(year));
        model.addAttribute("selectedYear", year);
        return "payment/list";
    }
    
    
    
}