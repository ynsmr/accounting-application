package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ReportingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class ReportingServiceImpl implements ReportingService {
    
    private final InvoiceProductService invoiceProductService;
    

    @Override
    public Map<String, BigDecimal> getMonthlyProfitLossReport() {
        Map<String, BigDecimal> monthlyProfitLossMap = new HashMap<>();
        
        invoiceProductService.listAllInvoiceProducts().stream()
                .filter(invoiceProductDto -> invoiceProductDto.getInvoice().getInvoiceType().equals(InvoiceType.SALES))
                .forEach(invoiceProductDto -> {
                    String monthYear = getMonthYearString(invoiceProductDto.getInvoice().getDate());
                    updateProfitLossForMonth(monthlyProfitLossMap, monthYear, invoiceProductDto.getProfitLoss());
                });
        
        return monthlyProfitLossMap;
    }

private void updateProfitLossForMonth(Map<String, BigDecimal> profitLossMap, String monthYear, BigDecimal profitLoss) {
    profitLossMap.computeIfAbsent(monthYear, k -> BigDecimal.ZERO);
    profitLossMap.put(monthYear, profitLossMap.get(monthYear).add(profitLoss));
}
    
    
    private String getMonthYearString(LocalDate localDate){
        return localDate.getMonth().name() +" - "+ localDate.getYear();
    }
    
    
    
    
}