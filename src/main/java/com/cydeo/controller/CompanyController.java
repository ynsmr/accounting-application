package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public String updateCompany(@Valid @ModelAttribute("company") CompanyDto companyDto, BindingResult bindingResult, @PathVariable("companyId") Long companyId){
        companyDto.setId(companyId);
        if (bindingResult.hasErrors()){
            return "company/company-update";
        }
       companyService.update(companyDto);
       return "redirect:/companies/list";
    }
    
    @PostMapping("/create")
    public String createCompany(@Valid @ModelAttribute("newCompany") CompanyDto companyDto, BindingResult bindingResult){
        
        if (bindingResult.hasErrors()){
            return "company/company-create";
        }
        companyService.save(companyDto);
        return "redirect:/companies/list";
    }
    
    @GetMapping("/delete/{companyId}")
    public String deleteCompany(@PathVariable("companyId") Long companyId){
        companyService.delete(companyId);
        return "redirect:/companies/list";
    }

    @GetMapping("/activate/{companyId}")
    public String activateCompany(@PathVariable Long companyId){
        companyService.activate(companyId);
        return "redirect:/companies/list"; 
    }

    @GetMapping("/deactivate/{companyId}")
    public String deactivateCompany(@PathVariable Long companyId){
        companyService.deactivate(companyId);
        return "redirect:/companies/list";
    }
}
