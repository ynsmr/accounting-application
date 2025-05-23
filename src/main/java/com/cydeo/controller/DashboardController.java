package com.cydeo.controller;

import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceService;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
    private final DashboardService dashboardService;
    private final InvoiceService invoiceService;

    public DashboardController(DashboardService dashboardService, InvoiceService invoiceService) {
        this.dashboardService = dashboardService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model){
       model.addAttribute("summaryNumbers", dashboardService.getSummaryNumbers());
       model.addAttribute("invoices", invoiceService.listLast3Approved());
        return "dashboard";
    }
}
