package com.cydeo.controller;

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
    
    @GetMapping("/create")
    public String createUserPage(){
        
        
        return "user/user-create";
    }
    
    
    @PostMapping("/create")
    public String createUser(Model model){
        
        
        return "redirect:/users/list";
    }

    @GetMapping("/update")
    public String updateUserPage(Model model){

        return "user/user-update";
    }
    
    @PostMapping("/update")
    public String updateUser(Model model){
        
        return "redirect:/users/update";
    }
    
    @GetMapping("/list")
    public String listUsers(Model model){
        model.addAttribute("users", userService.listAllUsers());
        
        return "user/user-list";
    }
    
    @GetMapping("/delete")
    public String deleteUser(){
        
        return "redirect:/users/list";
    }
    
}
