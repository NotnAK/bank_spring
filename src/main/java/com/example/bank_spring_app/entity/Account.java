package com.example.bank_spring_app.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private Double balance;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;
    @OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL)
    private List<Transaction> sentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "beneficiaryAccount", cascade = CascadeType.ALL)
    private List<Transaction> receivedTransactions = new ArrayList<>();

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", currency=" + currency +
                ", balance=" + balance +
                ", user=" + user +
                '}';
    }
}