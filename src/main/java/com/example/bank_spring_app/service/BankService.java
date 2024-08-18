package com.example.bank_spring_app.service;

import com.example.bank_spring_app.dto.AccountDTO;
import com.example.bank_spring_app.dto.TransactionDTO;
import com.example.bank_spring_app.dto.UserDTO;
import com.example.bank_spring_app.entity.*;
import com.example.bank_spring_app.exceprion.AccountNotFoundException;
import com.example.bank_spring_app.exceprion.InsufficientFundsException;
import com.example.bank_spring_app.mapper.EntityMapper;
import com.example.bank_spring_app.repository.AccountRepository;
import com.example.bank_spring_app.repository.RateOfExchangeRepository;
import com.example.bank_spring_app.repository.TransactionRepository;
import com.example.bank_spring_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankService {
    private final AccountRepository accountRepository;
    private final RateOfExchangeRepository rateOfExchangeRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    public BankService(AccountRepository accountRepository, RateOfExchangeRepository rateOfExchangeRepository,
                       TransactionRepository transactionRepository, UserRepository userRepository, EntityMapper entityMapper) {
        this.accountRepository = accountRepository;
        this.rateOfExchangeRepository = rateOfExchangeRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;

    }

    @Transactional(readOnly = true)
    public CustomUser findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Transactional(readOnly = true)
    public UserDTO findUserDTOByLogin(String login) {
        CustomUser customUser = userRepository.findByLogin(login);
        return entityMapper.toUserDto(customUser);
    }

    @Transactional
    public UserDTO addUser(UserDTO userDTO) {
        /*User user = new User();
        user.setName(userDTO.getName());
        user.setAddress(userDTO.getAddress());
        User savedUser = userRepository.save(user);
        userDTO.setId(savedUser.getId());
        return userDTO;*/
        CustomUser user = entityMapper.toUserEntity(userDTO);
        CustomUser savedUser = userRepository.save(user);
        return entityMapper.toUserDto(savedUser);
    }

    @Transactional
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public AccountDTO addAccount(AccountDTO accountDTO) {
        /*Account account = new Account();
        User user = userRepository.findById(accountDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        account.setUser(user);
        account.setCurrency(accountDTO.getCurrency());
        account.setBalance(accountDTO.getBalance());
        Account savedAccount = accountRepository.save(account);
        accountDTO.setId(savedAccount.getId());
        return accountDTO;*/
        CustomUser user = userRepository.findById(accountDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = entityMapper.toAccountEntity(accountDTO, user);
        Account savedAccount = accountRepository.save(account);
        return entityMapper.toAccountDto(savedAccount);
    }

    @Transactional
    public void deleteAccount(int accountId) {
        accountRepository.deleteById(accountId);
    }


    @Transactional
    public void depositFunds(int accountId, double amount) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);
            Transaction transaction = new Transaction(account, account, amount);
            transactionRepository.save(transaction);
        } else {
            throw new AccountNotFoundException("Account not found!");
        }
    }

    @Transactional
    public void transferFunds(int sourceAccountId, int destinationAccountId, double amount) {
        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Source account not found!"));

        Account destinationAccount = accountRepository.findById(destinationAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found!"));

        if (sourceAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Transfer failed. Insufficient funds");
        }

        RateOfExchange sourceRate = rateOfExchangeRepository.findByCurrency(sourceAccount.getCurrency());
        RateOfExchange destinationRate = rateOfExchangeRepository.findByCurrency(destinationAccount.getCurrency());

        if (sourceRate == null || destinationRate == null) {
            throw new RuntimeException("Currency conversion rates not found.");
        }

        double amountInBaseCurrency = amount * sourceRate.getRateToUAH();
        double convertedAmount = amountInBaseCurrency / destinationRate.getRateToUAH();

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + convertedAmount);

        Transaction transaction = new Transaction(sourceAccount, destinationAccount, amount);
        sourceAccount.getSentTransactions().add(transaction);
        destinationAccount.getReceivedTransactions().add(transaction);

        /*accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);*/

        transactionRepository.save(transaction);

/*        System.out.println("Funds transferred successfully.");*/
    }

    @Transactional
    public void convertCurrency(int userId, int sourceAccountId, int destinationAccountId, double amount) {
        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Source account not found!"));

        Account targetAccount = accountRepository.findById(destinationAccountId)
                .orElseThrow(() ->  new AccountNotFoundException("Destination account not found!"));
        if (sourceAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Transfer failed. Insufficient funds");
        }
        if (sourceAccount.getUser().getId() != userId || targetAccount.getUser().getId() != userId) {
            throw new RuntimeException("Conversion failed. Accounts do not belong to the same user.");
        }

        RateOfExchange sourceRate = rateOfExchangeRepository.findByCurrency(sourceAccount.getCurrency());
        RateOfExchange targetRate = rateOfExchangeRepository.findByCurrency(targetAccount.getCurrency());

        if (sourceRate == null || targetRate == null) {
            throw new RuntimeException("Currency conversion rates not found.");
        }

        double amountInBaseCurrency = amount * sourceRate.getRateToUAH();
        double convertedAmount = amountInBaseCurrency / targetRate.getRateToUAH();

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + convertedAmount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction transaction = new Transaction(sourceAccount, targetAccount, convertedAmount);
        transactionRepository.save(transaction);

        /*System.out.println("Currency converted successfully.");*/
    }

    @Transactional
    public double getTotalFundsInUAH(int userId) {
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        RateOfExchange uahRate = rateOfExchangeRepository.findByCurrency(Currency.UAH);

        if (uahRate == null) {
            throw new RuntimeException("UAH exchange rate not found.");
        }

        return userAccounts.stream()
                .mapToDouble(account -> {
                    RateOfExchange accountRate = rateOfExchangeRepository.findByCurrency(account.getCurrency());
                    if (accountRate != null) {
                        return account.getBalance() * accountRate.getRateToUAH();
                    }
                    return 0.0;
                })
                .sum();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> viewAllUsers() {
        List<CustomUser> users = userRepository.findAll();
        return users.stream()
                .map(entityMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> viewAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(entityMapper::toAccountDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> viewAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(entityMapper::toTransactionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTransaction(int transactionId) {
        transactionRepository.deleteById(transactionId);
    }
    /*// Вспомогательный метод для конвертации User в UserDTO
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setAddress(user.getAddress());
        // Конвертируем аккаунты пользователя в AccountDTO и добавляем их в UserDTO
        List<AccountDTO> accountDTOs = user.getAccountList().stream()
                .map(this::convertToAccountDTO)
                .collect(Collectors.toList());
        userDTO.setAccounts(accountDTOs);
        return userDTO;
    }

    // Вспомогательный метод для конвертации Account в AccountDTO
    private AccountDTO convertToAccountDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setCurrency(account.getCurrency());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setUserId(account.getUser().getId());
        return accountDTO;
    }*/
}
