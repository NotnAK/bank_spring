package com.example.bank_spring_app.repository;

import com.example.bank_spring_app.entity.Currency;
import com.example.bank_spring_app.entity.RateOfExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RateOfExchangeRepository extends JpaRepository<RateOfExchange, Integer> {
    @Query("SELECT r FROM RateOfExchange r WHERE r.currency = :currency")
    RateOfExchange findByCurrency(@Param("currency") Currency currency);
}
