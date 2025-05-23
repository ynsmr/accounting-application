package com.cydeo.service;

import java.math.BigDecimal;
import java.util.Map;

public interface DashboardService {
    
    BigDecimal calculateTotalCostByCompanyId();
    BigDecimal calculateTotalSalesByCompanyId();
    BigDecimal calculateProfitLossByCompanyId();
    Map<String, BigDecimal> getSummaryNumbers();
    
}
