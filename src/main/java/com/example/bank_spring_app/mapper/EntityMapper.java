package com.example.bank_spring_app.mapper;

import com.example.bank_spring_app.dto.AccountDTO;
import com.example.bank_spring_app.dto.TransactionDTO;
import com.example.bank_spring_app.dto.UserDTO;
import com.example.bank_spring_app.entity.Account;
import com.example.bank_spring_app.entity.CustomUser;
import com.example.bank_spring_app.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class EntityMapper {
    public UserDTO toUserDto(CustomUser user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        userDTO.setName(user.getName());
        userDTO.setAddress(user.getAddress());
        userDTO.setRole(user.getRole());
        userDTO.setPassword(user.getPassword());
        List<AccountDTO> accountDTOs = user.getAccountList().stream()
                .map(this::toAccountDto)
                .collect(Collectors.toList());
        userDTO.setAccounts(accountDTOs);

        return userDTO;
    }

    public AccountDTO toAccountDto(Account account) {
        if (account == null) {
            return null;
        }
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setCurrency(account.getCurrency());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setUserId(account.getUser().getId());

        return accountDTO;
    }

    public CustomUser toUserEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        CustomUser user = new CustomUser();
        user.setId(userDTO.getId());
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole());
        return user;
    }

    public Account toAccountEntity(AccountDTO accountDTO, CustomUser user) {
        if (accountDTO == null) {
            return null;
        }
        Account account = new Account();
        account.setId(accountDTO.getId());
        account.setCurrency(accountDTO.getCurrency());
        account.setBalance(accountDTO.getBalance());
        account.setUser(user);

        return account;
    }
    public TransactionDTO toTransactionDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setSenderAccountId(transaction.getSenderAccount().getId());
        transactionDTO.setBeneficiaryAccountId(transaction.getBeneficiaryAccount().getId());
        transactionDTO.setAmount(transaction.getAmount());

        return transactionDTO;
    }
}
