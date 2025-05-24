package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReportingServiceImpl implements ReportingService {
    
    private final InvoiceProductService invoiceProductService;
    private final InvoiceService invoiceService;

    public ReportingServiceImpl(InvoiceProductService invoiceProductService, InvoiceService invoiceService) {
        this.invoiceProductService = invoiceProductService;
        this.invoiceService = invoiceService;
    }

    @Override
    public Map<String, BigDecimal> getMonthlyProfitLossReport() {
        Map<String, BigDecimal> monthlyProfitLossMap = new HashMap<>();
        
        
        
        
        
        
        
        return monthlyProfitLossMap;
    }
    
    
    private String getMonthYearString(LocalDate localDate){
        return localDate.getMonth().name() + localDate.getYear();
    }
    
    
    
    
}
