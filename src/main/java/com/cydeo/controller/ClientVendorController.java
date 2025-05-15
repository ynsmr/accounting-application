package com.cydeo.controller;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;

    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }
    
    @GetMapping("/list")
    public String listClientVendors(Model model){
        
        return "/clientVendor-list";
    }
    
    @GetMapping("/create")
    public String createClientVendorPage(Model model){
        model.addAttribute("newClientVendor", new ClientVendorDto());
        
        return "clientVendor/clientVendor-create";
    }

    @GetMapping("/update/{clientVendorId}")
    public String updateClientVendorPage(@PathVariable("clientVendorId") Long clientVendorId, Model model){
        

        return "clientVendor/clientVendor-update";
    }
    
    @GetMapping("/delete/{clientVendorId}")
    public String deleteClientVendor(@PathVariable("clientVendorId") Long clientVendorId){
        
        return "redirect:/clientVendors/list";
    }

    @PostMapping("/create")
    public String createClientVendor(@ModelAttribute("clientVendor") ClientVendorDto clientVendorDto){
        
        return "redirect:/clientVendors/list";
    }

    @PostMapping("/update/{clientVendorId}")
    public String updateClientVendor(@ModelAttribute("clientVendor") ClientVendorDto clientVendorDto, @PathVariable("clientVendorId") Long clientVendorId){
        clientVendorDto.setId(clientVendorId);
        
        return "redirect:/clientVendors/list";
    }
    
    

    
}
