package com.example.bank_spring_app.controller;

import com.example.bank_spring_app.dto.AccountDTO;
import com.example.bank_spring_app.dto.TransactionDTO;
import com.example.bank_spring_app.dto.UserDTO;
import com.example.bank_spring_app.entity.Currency;
import com.example.bank_spring_app.entity.UserRole;
import com.example.bank_spring_app.exceprion.AccountNotFoundException;
import com.example.bank_spring_app.exceprion.InsufficientFundsException;
import com.example.bank_spring_app.service.BankService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private final BankService bankService;
    private final PasswordEncoder passwordEncoder;

    public UserController(BankService bankService, PasswordEncoder passwordEncoder) {
        this.bankService = bankService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/userMenu")
    public String userMenu(Model model){
        User currentUser = getCurrentUser();
        UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());

        model.addAttribute("user", userDTO);
        model.addAttribute("accounts", userDTO.getAccounts());
        model.addAttribute("currencies", Currency.values());
        return "userMenu";
    }
    @GetMapping("/transactions")
    public String viewTransactions(Model model) {
        User currentUser = getCurrentUser();
        UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());

        List<Integer> userAccountIds = userDTO.getAccounts().stream()
                .map(AccountDTO::getId)
                .collect(Collectors.toList());

        // Фильтруем транзакции, где либо отправитель, либо получатель — аккаунт пользователя
        List<TransactionDTO> transactions = bankService.viewAllTransactions().stream()
                .filter(transaction -> userAccountIds.contains(transaction.getSenderAccountId()) ||
                        userAccountIds.contains(transaction.getBeneficiaryAccountId()))
                .collect(Collectors.toList());

        model.addAttribute("transactions", transactions);

        return "userTransactions";
    }
    @PostMapping("/deposit")
    public String depositFunds(@RequestParam int accountId, @RequestParam double amount) {
        bankService.depositFunds(accountId, amount);
        return "redirect:/user/userMenu";
    }
    @GetMapping("/deposit")
    public String showDepositForm(Model model) {
        User currentUser = getCurrentUser();
        UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());

        model.addAttribute("accounts", userDTO.getAccounts());
        return "userDeposit";  // Возвращаем имя шаблона с формой
    }
    @PostMapping("/transfer")
    public String transferFunds(@RequestParam int sourceAccountId,
                                @RequestParam int destinationAccountId,
                                @RequestParam double amount,
                                Model model) {
        try {
            bankService.transferFunds(sourceAccountId, destinationAccountId, amount);
            return "redirect:/user/userMenu";
        } catch (RuntimeException ex) {
            User currentUser = getCurrentUser();
            UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());
            model.addAttribute("accounts", userDTO.getAccounts());
            model.addAttribute("errorMessage", ex.getMessage());
            return "userTransfer";
        }
    }
    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        User currentUser = getCurrentUser();
        UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());

        model.addAttribute("accounts", userDTO.getAccounts());
        return "userTransfer";
    }

    @PostMapping("/convert")
    public String convertCurrency(@RequestParam int sourceAccountId,
                                  @RequestParam int targetAccountId,
                                  @RequestParam double amount,
                                  Model model) {
        try{
            User currentUser = getCurrentUser();
            UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());
            bankService.convertCurrency(userDTO.getId(), sourceAccountId, targetAccountId, amount);
            return "redirect:/user/userMenu";
        }
        catch (RuntimeException ex){
            model.addAttribute("errorMessage", ex.getMessage());
            User currentUser = getCurrentUser();
            UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());
            model.addAttribute("accounts", userDTO.getAccounts());
            return "userConvert";
        }
    }
    @GetMapping("/convert")
    public String showConvertForm(Model model) {
        User currentUser = getCurrentUser();
        UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());
        model.addAttribute("accounts", userDTO.getAccounts());
        return "userConvert";
    }
    @PostMapping("/addAccount")
    public String addAccount(@RequestParam Currency currency) {
        User currentUser = getCurrentUser();
        UserDTO userDTO = bankService.findUserDTOByLogin(currentUser.getUsername());

        AccountDTO newAccount = new AccountDTO();
        newAccount.setCurrency(currency);
        newAccount.setBalance(0.0);
        newAccount.setUserId(userDTO.getId());

        bankService.addAccount(newAccount);
        return "redirect:/user/userMenu";
    }
    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
