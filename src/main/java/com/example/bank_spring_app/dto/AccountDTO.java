package com.example.bank_spring_app.dto;

import com.example.bank_spring_app.entity.Currency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
    private Integer id;
    private Currency currency;
    private Double balance;
    private Integer userId;

    public AccountDTO() {
    }

    public AccountDTO(Currency currency, Double balance, Integer userId) {
        this.currency = currency;
        this.balance = balance;
        this.userId = userId;
    }
}
