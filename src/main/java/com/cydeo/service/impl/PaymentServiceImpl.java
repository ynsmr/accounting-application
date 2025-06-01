package com.cydeo.service.impl;

import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.Payment;
import com.cydeo.enums.Month;
import com.cydeo.exception.InvalidYearException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.respository.PaymentRepository;
import com.cydeo.service.PaymentService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final MapperUtil mapperUtil;
    
    @Override
    public List<PaymentDto> listAllPayments() {
        if (!paymentRepository.existsByYear(Year.now().getValue())){
            createPayments(Year.now().getValue());
        }
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .map(this::convertToDto)
                .sorted(Comparator.comparing(PaymentDto::getYear).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> listPaymentsByYear(int year) {
        if (!yearIsValid(year)){
            throw new InvalidYearException("Year selected is not valid for the company.");
        }
        if (!paymentRepository.existsByYear(year)){
            createPayments(year);
        }
        
        return paymentRepository.findPaymentByYear(year).stream()
                .filter(payment -> payment.getCompany().getId().equals(userService.getLoggedInUser().getCompany().getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void savePayment(PaymentDto paymentDto) {
        paymentDto.setCompany(userService.getLoggedInUser().getCompany());
        paymentDto.setYear(Year.now().getValue());
        paymentDto.setMonth(Month.valueOf(LocalDate.now().getMonth().name()));
        paymentRepository.save(convertToEntity(paymentDto));
    }

    private PaymentDto convertToDto(Payment payment){
        return mapperUtil.convert(payment, new PaymentDto());
    }
    
    private Payment convertToEntity(PaymentDto paymentDto){
        return mapperUtil.convert(paymentDto, new Payment());
    }
    
    private List<Payment> createPayments(int year){
        List<Payment> payments = new ArrayList<>();
        Arrays.stream(Month.values())
                .forEach(month -> {
                    Payment newPayment = new Payment();
                    newPayment.setYear(year);
                    newPayment.setMonth(month);
                    newPayment.setCompany(userService.getLoggedInUser().getCompany());
                    newPayment.setAmount(BigDecimal.valueOf(250L));
                    payments.add(newPayment);
                    paymentRepository.save(newPayment);
                });
        return payments;
    }
    
    private boolean yearIsValid(int year){
        return userService.getLoggedInUser().getCompany().getInsertDateTime().getYear() <= year;
    }
    
    
}
