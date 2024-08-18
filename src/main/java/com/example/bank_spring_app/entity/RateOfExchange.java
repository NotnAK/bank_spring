package com.example.bank_spring_app.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rates_of_exchange")
public class RateOfExchange {
    @Id
    @Enumerated(EnumType.STRING)
    private Currency currency;

    private double rateToUAH;

    public RateOfExchange() {
    }

    public RateOfExchange(Currency currency, double rateToUAH) {
        this.currency = currency;
        this.rateToUAH = rateToUAH;
    }

}
