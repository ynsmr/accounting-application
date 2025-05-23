package com.cydeo.controller;
import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;

    public UserController(UserService userService, RoleService roleService, CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }

    @GetMapping("/create")
    public String createUserPage(Model model){
        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.listAllCompanies());
        return "user/user-create";
    }
    
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("newUser") UserDto userDto, BindingResult bindingResult, Model model){
        
        
        if (bindingResult.hasErrors()){
            model.addAttribute("userRoles", roleService.listAllRoles());
            model.addAttribute("companies", companyService.listAllCompanies());
            return "user/user-create";
        }
        userService.saveUser(userDto);
        return "redirect:/users/list";
    }

    @GetMapping("/update/{userId}")
    public String updateUserPage(@PathVariable("userId") Long userId, Model model){
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.listAllCompanies());
        return "user/user-update";
    }
    
    @PostMapping("/update/{userId}")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, @PathVariable("userId") Long userId, Model model){
        userDto.setId(userId);
        if (bindingResult.hasErrors()){
            model.addAttribute("userRoles", roleService.listAllRoles());
            model.addAttribute("companies", companyService.listAllCompanies());
            return "user/user-update";
        }
        userService.updateUser(userDto);
        return "redirect:/users/list";
    }
    
    @GetMapping("/list")
    public String listUsers(Model model){
        model.addAttribute("users", userService.listAllUsers());
        return "user/user-list";
    }
    
    @GetMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId){
        userService.deleteById(userId);
        return "redirect:/users/list";
    }
    
  
    
}
