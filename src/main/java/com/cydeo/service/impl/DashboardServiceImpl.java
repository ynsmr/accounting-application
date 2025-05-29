package com.cydeo.service.impl;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final InvoiceService invoiceService;
    
    // Extract constants for map keys
    private static final String KEY_TOTAL_COST = "totalCost";
    private static final String KEY_TOTAL_SALES = "totalSales";
    private static final String KEY_PROFIT_LOSS = "profitLoss";
    
    @Override
    public BigDecimal calculateTotalCostByCompanyId() {
        return invoiceService.retrieveCurrentPurchaseInvoices().stream()
                .filter(invoiceDto -> invoiceDto.getInvoiceStatus().equals(InvoiceStatus.APPROVED))
                .map(InvoiceDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalSalesByCompanyId() {
        return invoiceService.retrieveCurrentSalesInvoices().stream()
                .filter(invoiceDto -> invoiceDto.getInvoiceStatus().equals(InvoiceStatus.APPROVED))
                .map(InvoiceDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateProfitLossByCompanyId() {
        return calculateTotalSalesByCompanyId().subtract(calculateTotalCostByCompanyId());
    }

    @Override
    public Map<String, BigDecimal> getSummaryNumbers() {
        Map<String, BigDecimal> summaryMap = new HashMap<>();
        summaryMap.put(KEY_TOTAL_COST, calculateTotalCostByCompanyId());
        summaryMap.put(KEY_TOTAL_SALES, calculateTotalSalesByCompanyId());
        summaryMap.put(KEY_PROFIT_LOSS, calculateProfitLossByCompanyId());
        return summaryMap;
    }
}