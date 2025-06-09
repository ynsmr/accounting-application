package com.cydeo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportingServiceTest {
    @Mock
    private InvoiceProductService invoiceProductService;
    
    @Test
    void should_get_monthly_profit_loss(){
        Map<String, BigDecimal> monthlyProfitLossMap = new HashMap<>();
        monthlyProfitLossMap.put("June", BigDecimal.valueOf(34564L));
        monthlyProfitLossMap.put("July", BigDecimal.valueOf(48764L));
        monthlyProfitLossMap.put("January", BigDecimal.valueOf(7642L));
        
        
        verify(invoiceProductService).listAllInvoiceProducts();
    }
    
}
