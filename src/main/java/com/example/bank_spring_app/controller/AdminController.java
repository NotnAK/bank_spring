package com.example.bank_spring_app.controller;

import com.example.bank_spring_app.dto.TransactionDTO;
import com.example.bank_spring_app.service.BankService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final BankService bankService;
    private final PasswordEncoder passwordEncoder;
    public AdminController(BankService bankService, PasswordEncoder passwordEncoder) {
        this.bankService = bankService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/adminMenu")
    public String adminMenu(){
        return "adminMenu";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", bankService.viewAllUsers());
        return "users";
    }
    @GetMapping("/accounts")
    public String viewAccounts(Model model) {
        model.addAttribute("accounts", bankService.viewAllAccounts());
        return "accounts";  // Вернет страницу со списком аккаунтов
    }
    @GetMapping("/transactions")
    public String viewTransactions(Model model) {
        List<TransactionDTO> transactions = bankService.viewAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transactions";
    }
    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id) {
        bankService.deleteUser(id);
        return "redirect:/admin/users";  // Перенаправит на страницу со списком пользователей после удаления
    }

    // Удаление аккаунта по ID
    @PostMapping("/deleteAccount/{id}")
    public String deleteAccount(@PathVariable int id) {
        bankService.deleteAccount(id);
        return "redirect:/admin/accounts";  // Перенаправит на страницу со списком аккаунтов после удаления
    }
    @PostMapping("/deleteTransaction/{id}")
    public String deleteTransaction(@PathVariable int id) {
        bankService.deleteTransaction(id);
        return "redirect:/admin/transactions";  // Перенаправит обратно на список транзакций после удаления
    }
}
