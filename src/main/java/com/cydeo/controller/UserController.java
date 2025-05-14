package com.cydeo.controller;

import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/create")
    public String createUserPage(Model model){
        
        return null;
    }
    
    @PostMapping("/create")
    public String createUser(Model model){
        
        
        return null;
    }

    @PutMapping("/update")
    public String updateUserPage(Model model){

        return null;
    }
    
    @PutMapping("/update")
    public String updateUser(Model model){
        
        return null;
    }
    
    @GetMapping("/list")
    public String listUsers(){
        
        return null;
    }
    
    @DeleteMapping("/delete")
    public String deleteUser(){
        
        return null;
    }
    
}
