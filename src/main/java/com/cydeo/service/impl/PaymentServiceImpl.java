package com.cydeo.service.impl;

import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.Payment;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.PaymentRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.PaymentService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final CompanyService companyService;
    private final MapperUtil mapperUtil;
    
    @Override
    public List<PaymentDto> listAllPayments() {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .map(this::convertToDto)
                .sorted(Comparator.comparing(PaymentDto::getYear).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> listPaymentsByYear(int year) {
        return paymentRepository.findPaymentByYear(year).stream()
                .filter(payment -> payment.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private PaymentDto convertToDto(Payment payment){
        return mapperUtil.convert(payment, new PaymentDto());
    }
    
    private Payment convertToEntity(PaymentDto paymentDto){
        return mapperUtil.convert(paymentDto, new Payment());
    }
    
    private List<Payment> createPayments(int year){
        return null;
    }
    
    
}
