package com.cydeo.controller;

import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;
    
    @GetMapping("/create")
    public String createUserPage(Model model){
        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.listAllCompanies());
        return "user/user-create";
    }
    
    
    @PostMapping("/create")
    public String createUser(@ModelAttribute("newUser") UserDto userDto){
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
    public String updateUser(@ModelAttribute("user") UserDto userDto){
        userService.saveUser(userDto);
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
