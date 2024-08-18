package com.example.bank_spring_app.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity()
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "sender_account_id")
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "beneficiary_account_id")
    private Account beneficiaryAccount;

    private Double amount;

    public Transaction() {
    }

    public Transaction(Account senderAccount, Account beneficiaryAccount, Double amount) {
        this.amount = amount;
        this.beneficiaryAccount = beneficiaryAccount;
        this.senderAccount = senderAccount;
    }
}
