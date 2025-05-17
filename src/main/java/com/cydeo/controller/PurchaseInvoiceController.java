package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchaseInvoiceController {
    
    @GetMapping("/list")
    public String listPurchaseInvoicePage(Model model){
        
        return "/invoice/purchase-invoice-list";
    }
    
    @GetMapping("/create")
    public String createPurchaseInvoicePage(Model model){
        
        return "/invoice/purchase-invoice-create";
    }
    
    @GetMapping("/update/{invoiceId}")
    public String updatePurchaseInvoicePage(@PathVariable("invoiceId") Long invoiceId, Model model){
        
        return "/invoice/purchase-invoice-update";
    }
    
    @GetMapping("/delete/{invoiceId}")
    public String deletePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId){
        
        return "redirect:/purchasesInvoice/list";
    }
    
    @PostMapping("/create")
    public String createPurchaseInvoice(@ModelAttribute("purchaseInvoice")InvoiceDto invoiceDto){

        return "redirect:/purchasesInvoice/list";
    }
    
    @PostMapping("/update/{invoiceId}")
    public String updatePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, @ModelAttribute("purchaseInvoice") InvoiceDto invoiceDto){

        return "redirect:/purchasesInvoice/list";
    }
}
