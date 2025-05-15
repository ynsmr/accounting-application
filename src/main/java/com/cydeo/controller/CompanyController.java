package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    
    @GetMapping("/list")
    public String companyList(Model model){
        model.addAttribute("companies", companyService.listAllCompanies());
        return "company/company-list";
    }
    
    @GetMapping("/create")
    public String createCompanyPage(Model model){
        model.addAttribute("newCompany", new CompanyDto());
        return "company/company-create";
    }
    
    @GetMapping("/update/{companyId}")
    public String updateCompanyPage(@PathVariable("companyId") Long companyId, Model model){
        model.addAttribute("company" ,companyService.findById(companyId));
        return "company/company-update";
    }
    
    @PostMapping("/update/{companyId}")
    public String updateCompany(@PathVariable("companyId") Long companyId, @ModelAttribute("company") CompanyDto companyDto){
       companyDto.setId(companyId);
       companyService.updateCompany(companyDto);
       return "redirect:/companies/list";
    }
    
    @PostMapping("/create")
    public String createCompany(@ModelAttribute CompanyDto companyDto){
        companyService.saveCompany(companyDto);
        return "redirect:/companies/list";
    }
    
    @GetMapping("/delete/{companyId}")
    public String deleteCompany(@PathVariable("companyId") Long companyId){
        companyService.deleteCompany(companyId);
        return "redirect:/companies/list";
    }

    @GetMapping("/activate/{companyId}")
    public String activateCompany(@PathVariable Long companyId){
        companyService.activateCompany(companyId);
        return "redirect:/companies/list"; 
    }

    @GetMapping("/deactivate/{companyId}")
    public String deactivateCompany(@PathVariable Long companyId){
        companyService.deactivateCompany(companyId);
        return "redirect:/companies/list";
    }
}
