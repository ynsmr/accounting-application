package com.cydeo.controller;

import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportingController {
    private final InvoiceProductService invoiceProductService;
    private final ReportingService reportingService;

    public ReportingController(InvoiceProductService invoiceProductService, ReportingService reportingService) {
        this.invoiceProductService = invoiceProductService;
        this.reportingService = reportingService;
    }

    @GetMapping("/stockData")
    public String stockData(Model model){
        model.addAttribute("invoiceProducts", invoiceProductService.listAllInvoiceProducts());
        return "report/stock-report";
    }
    
    @GetMapping("/profitLossData")
    public String profitLossData(Model model){
        model.addAttribute("monthlyProfitLossDataMap", reportingService.getMonthlyProfitLossReport());
        return "report/profit-loss-report";
    }   
}
