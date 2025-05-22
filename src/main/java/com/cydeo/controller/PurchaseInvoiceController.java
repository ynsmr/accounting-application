package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchaseInvoiceController {
    
    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;

    public PurchaseInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
    }

    @GetMapping("/list")
    public String listPurchaseInvoicePage(Model model){
        model.addAttribute("invoices", invoiceService.retrieveCurrentPurchaseInvoices());
        return "invoice/purchase-invoice-list";
    }
    
    @GetMapping("/create")
    public String createPurchaseInvoicePage(Model model){
        model.addAttribute("newPurchaseInvoice",invoiceService.getInvoiceTemplate(InvoiceType.PURCHASE));
        model.addAttribute("vendors", clientVendorService.listAllByType(ClientVendorType.VENDOR));
        
        return "invoice/purchase-invoice-create";
    }
    
    @GetMapping("/update/{invoiceId}")
    public String updatePurchaseInvoicePage(@PathVariable("invoiceId") Long invoiceId, Model model){
        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        model.addAttribute("vendors", clientVendorService.listAllByType(ClientVendorType.VENDOR));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("invoiceProducts", invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId));
        return "invoice/purchase-invoice-update";
    }
    
    @GetMapping("/delete/{invoiceId}")
    public String deletePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId){
        invoiceService.deleteInvoiceById(invoiceId);
        return "redirect:/purchaseInvoices/list";
    }
    
    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProduct(@PathVariable("invoiceId") Long invoiceId, @PathVariable("invoiceProductId") Long invoiceProductId){
        invoiceProductService.removeInvoiceProduct(invoiceId, invoiceProductId);
        return "redirect:/purchaseInvoices/update/{invoiceId}";
    }
    
    @GetMapping("/print/{invoiceId}")
    public String printPurchaseInvoicePage(@PathVariable("invoiceId") Long invoiceId, Model model){
        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        model.addAttribute("company", invoiceService.findById(invoiceId).getCompany());
        model.addAttribute("invoiceProducts", invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId));
        
        return "invoice/invoice-print";
    }
    
    @GetMapping("/approve/{invoiceId}")
    public String approvePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId){
        invoiceService.approvePurchaseInvoice(invoiceId);
        return "redirect:/purchaseInvoices/list";
    }
    
    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProduct(@Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult bindingResult, @PathVariable("invoiceId") Long invoiceId, Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("vendors", clientVendorService.listAllByType(ClientVendorType.VENDOR));
            model.addAttribute("products", productService.listAllProducts());
            model.addAttribute("invoiceProducts", invoiceProductService.findInvoiceProductsByInvoiceId(invoiceId));
            return "invoice/purchase-invoice-update";
        }
        invoiceProductService.addInvoiceProduct(invoiceProductDto, invoiceId);
        return "redirect:/purchaseInvoices/update/{invoiceId}";
    }
    
    @PostMapping("/create")
    public String createPurchaseInvoice(@ModelAttribute("purchaseInvoice")InvoiceDto invoiceDto){
        invoiceService.saveInvoice(invoiceDto);
        return "redirect:/purchaseInvoices/list";
    }
    
    @PostMapping("/update/{invoiceId}")
    public String updatePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, @ModelAttribute("purchaseInvoice") InvoiceDto invoiceDto){
        invoiceDto.setId(invoiceId);
        invoiceService.saveInvoice(invoiceDto);
        return "redirect:/purchaseInvoices/list";
    }



}
