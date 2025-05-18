package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ProductService productService;
    private final ClientVendorService clientVendorService;

    public SalesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ProductService productService, ClientVendorService clientVendorService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listSalesInvoicePage(Model model){
        model.addAttribute("invoices", invoiceService.listAllInvoices());
        return "/invoice/sales-invoice-list";
    }
    
    @GetMapping("/create")
    public String createSalesInvoicePage(Model model){
        model.addAttribute("newSalesInvoice",invoiceService.getInvoiceTemplate(ClientVendorType.CLIENT));
        model.addAttribute("clients", clientVendorService.listAllByType(ClientVendorType.CLIENT));
        return "/invoice/sales-invoice-create";
    }
    
    @GetMapping("/update/{invoiceId}")
    public String updateSalesInvoicePage(@PathVariable("invoiceId") Long invoiceId, Model model){
        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        model.addAttribute("clients", clientVendorService.listAllByType(ClientVendorType.CLIENT));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId));
        return "/invoice/sales-invoice-update";
    }
    
    @GetMapping("/delete/{invoiceId}")
    public String deleteSalesInvoice(@PathVariable("invoiceId") Long invoiceId){
        invoiceService.deleteInvoiceById(invoiceId);
        return "redirect:/salesInvoices/list";
    }

    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProduct(@PathVariable("invoiceId") Long invoiceId, @PathVariable("invoiceProductId") Long invoiceProductId){
        invoiceProductService.removeInvoiceProduct(invoiceId, invoiceProductId);
        return "redirect:/salesInvoices/update/{invoiceId}";
    }

    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProduct(@PathVariable("invoiceId") Long invoiceId, @ModelAttribute("invoiceProduct") InvoiceProductDto invoiceProductDto){
        invoiceProductService.addInvoiceProduct(invoiceProductDto, invoiceId);
        return "redirect:/salesInvoices/update/{invoiceId}";
    }
    
    @PostMapping("/create")
    public String createSalesInvoice(@ModelAttribute("salesInvoice")InvoiceDto invoiceDto){
        invoiceService.saveInvoice(invoiceDto);
        return "redirect:/salesInvoices/list";
    }
    
    @PostMapping("/update/{invoiceId}")
    public String updateSalesInvoice(@PathVariable("invoiceId") Long invoiceId, @ModelAttribute("salesInvoice") InvoiceDto invoiceDto){
        invoiceDto.setId(invoiceId);
        invoiceService.saveInvoice(invoiceDto);
        return "redirect:/salesInvoices/list";
    }
}
