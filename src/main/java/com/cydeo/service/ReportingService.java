package com.cydeo.service;

import java.math.BigDecimal;
import java.util.Map;

public interface ReportingService {
    
    Map<String, BigDecimal> getMonthlyProfitLossReport();
}
