package com.example.bank_spring_app.controller;

import com.example.bank_spring_app.dto.UserDTO;
import com.example.bank_spring_app.entity.UserRole;
import com.example.bank_spring_app.service.BankService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequestMapping("/")
public class LoginController {
    private final BankService bankService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(BankService bankService, PasswordEncoder passwordEncoder) {
        this.bankService = bankService;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/registerUser")
    public String registerUser(@RequestParam String login, @RequestParam String password, @RequestParam String name, @RequestParam String address) {
        UserDTO userDTO = new UserDTO(login, passwordEncoder.encode(password), UserRole.USER, name, address);
        bankService.addUser(userDTO);
        return "login";
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    @GetMapping("/unauthorized")
    public String unauthorizedPage(Model model) {
        User user = getCurrentUser();
        model.addAttribute("login", user.getUsername());
        return "unauthorized";
    }
    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
