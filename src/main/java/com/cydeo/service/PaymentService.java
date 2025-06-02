package com.cydeo.service;

import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.Payment;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> listAllPayments();
    List<PaymentDto> listPaymentsByYear(int year);
    PaymentDto getPaymentById(Long id);
    void savePayment(PaymentDto paymentDto);
    void markAsPaid(Long id);
}