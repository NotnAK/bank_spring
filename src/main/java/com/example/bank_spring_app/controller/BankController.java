package com.example.bank_spring_app.controller;

import com.example.bank_spring_app.dto.AccountDTO;
import com.example.bank_spring_app.dto.UserDTO;
import com.example.bank_spring_app.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BankController(BankService bankService, PasswordEncoder passwordEncoder) {
        this.bankService = bankService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/createUser")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return bankService.addUser(userDTO);
    }

    @PostMapping("/createAccount")
    public AccountDTO createAccount(@RequestBody AccountDTO accountDTO) {
        return bankService.addAccount(accountDTO);
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return bankService.viewAllUsers();
    }

    @GetMapping("/accounts")
    public List<AccountDTO> getAllAccounts() {
        return bankService.viewAllAccounts();
    }
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        bankService.deleteUser(id);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "User deleted successfully"));
    }
    @PostMapping("/deposit")
    public String depositFunds(@RequestParam int accountId, @RequestParam double amount) {
        bankService.depositFunds(accountId, amount);
        return "Funds deposited successfully";
    }

    @PostMapping("/transfer")
    public String transferFunds(@RequestParam int sourceAccountId, @RequestParam int destinationAccountId, @RequestParam double amount) {
        bankService.transferFunds(sourceAccountId, destinationAccountId, amount);
        return "Funds transferred successfully";
    }

    @PostMapping("/convert")
    public String convertCurrency(@RequestParam int userId, @RequestParam int sourceAccountId, @RequestParam int targetAccountId, @RequestParam double amount) {
        bankService.convertCurrency(userId, sourceAccountId, targetAccountId, amount);
        return "Currency converted successfully";
    }

    @GetMapping("/totalFunds")
    public double getTotalFundsInUAH(@RequestParam int userId) {
        return bankService.getTotalFundsInUAH(userId);
    }
}
