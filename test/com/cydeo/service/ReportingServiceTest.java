package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.impl.ReportingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportingServiceTest {
    @Mock
    private InvoiceProductService invoiceProductService;
    
    @InjectMocks
    private ReportingServiceImpl reportingService;
    
    
    private List<InvoiceProductDto> getMockList(){
        // Mock data
        InvoiceProductDto invoiceProduct1 = new InvoiceProductDto();
        invoiceProduct1.setProfitLoss(new BigDecimal("100.50"));
        
        InvoiceDto invoiceDto1 = new InvoiceDto();
        invoiceDto1.setId(1L);
        invoiceDto1.setInvoiceType(InvoiceType.SALES);
        invoiceDto1.setDate(LocalDate.of(2023, 9, 15));
        invoiceProduct1.setInvoice(invoiceDto1);

        InvoiceProductDto invoiceProduct2 = new InvoiceProductDto();
        invoiceProduct2.setProfitLoss(new BigDecimal("200.75"));

        InvoiceDto invoiceDto2 = new InvoiceDto();
        invoiceDto2.setId(2L);
        invoiceDto2.setInvoiceType(InvoiceType.SALES);
        invoiceDto2.setDate(LocalDate.of(2023, 10, 15));
        invoiceProduct2.setInvoice(invoiceDto2);

        InvoiceProductDto invoiceProduct3 = new InvoiceProductDto();
        invoiceProduct3.setProfitLoss(new BigDecimal("-5056"));

        InvoiceDto invoiceDto3 = new InvoiceDto();
        invoiceDto3.setId(3L);
        invoiceDto3.setInvoiceType(InvoiceType.SALES);
        invoiceDto3.setDate(LocalDate.of(2023, 11, 15));
        invoiceProduct3.setInvoice(invoiceDto3);

        List<InvoiceProductDto> mockedInvoiceProducts = List.of(invoiceProduct1, invoiceProduct2, invoiceProduct3);
        return mockedInvoiceProducts;
        
    }
    
    
    @Test
    void should_get_monthly_profit_loss() {
        
    // Mock the behavior of invoiceProductService
    when(invoiceProductService.listAllInvoiceProducts()).thenReturn(getMockList());

    // Call the method under test
    Map<String, BigDecimal> result = reportingService.getMonthlyProfitLossReport();

    // Assertions
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(getMockList().get(0).getProfitLoss(), result.get("SEPTEMBER - 2023")); 
    assertEquals(getMockList().get(1).getProfitLoss(), result.get("OCTOBER - 2023")); 
    assertEquals(getMockList().get(2).getProfitLoss(), result.get("NOVEMBER - 2023")); 

    // Verify the interaction with mocked invoiceProductService
    verify(invoiceProductService).listAllInvoiceProducts();
}
    
}