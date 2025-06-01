package com.cydeo.controller;
import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;
    private final CompanyService companyService;

    public ClientVendorController(ClientVendorService clientVendorService, CompanyService companyService) {
        this.clientVendorService = clientVendorService;
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String listClientVendors(Model model){
        model.addAttribute("clientVendors", clientVendorService.listAll());
        return "clientVendor/clientVendor-list";
    }
    
    @GetMapping("/create")
    public String createClientVendorPage(Model model){
        model.addAttribute("newClientVendor", new ClientVendorDto());
        model.addAttribute("clientVendorTypes", Arrays.asList(ClientVendorType.values()));
        model.addAttribute("countries", companyService.getOfficialCountryNames());
        return "clientVendor/clientVendor-create";
    }

    @GetMapping("/update/{clientVendorId}")
    public String updateClientVendorPage(@PathVariable("clientVendorId") Long clientVendorId, Model model){
        model.addAttribute("clientVendor", clientVendorService.findById(clientVendorId));
        model.addAttribute("clientVendorTypes", Arrays.asList(ClientVendorType.values()));
        model.addAttribute("countries", companyService.getOfficialCountryNames());
        return "clientVendor/clientVendor-update";
    }
    
    @GetMapping("/delete/{clientVendorId}")
    public String deleteClientVendor(@PathVariable("clientVendorId") Long clientVendorId){
        clientVendorService.deleteClientVendor(clientVendorId);
        return "redirect:/clientVendors/list";
    }

    @PostMapping("/create")
    public String createClientVendor(@Valid @ModelAttribute("newClientVendor") ClientVendorDto clientVendorDto, BindingResult bindingResult, Model model){
        
        if (bindingResult.hasErrors()){
            model.addAttribute("clientVendorTypes", Arrays.asList(ClientVendorType.values()));
            model.addAttribute("countries", companyService.getOfficialCountryNames());
            return "clientVendor/clientVendor-create";
        }
        clientVendorService.saveClientVendor(clientVendorDto);
        return "redirect:/clientVendors/list";
    }

    @PostMapping("/update/{clientVendorId}")
    public String updateClientVendor(@Valid @ModelAttribute("clientVendor") ClientVendorDto clientVendorDto, BindingResult bindingResult, @PathVariable("clientVendorId") Long clientVendorId, Model model){
        clientVendorDto.setId(clientVendorId);
        
        if (bindingResult.hasErrors()){
            model.addAttribute("clientVendor", clientVendorDto);
            model.addAttribute("clientVendorTypes", Arrays.asList(ClientVendorType.values()));
            model.addAttribute("countries", companyService.getOfficialCountryNames());
            return "clientVendor/clientVendor-update";
        }
        clientVendorService.updateClientVendor(clientVendorDto);
        return "redirect:/clientVendors/list";
    }
    
    

    
}
