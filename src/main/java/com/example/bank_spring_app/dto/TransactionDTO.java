package com.example.bank_spring_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {
    private Integer id;
    private Integer senderAccountId;
    private Integer beneficiaryAccountId;
    private Double amount;
}
