package com.example.bank.dto;

import jakarta.validation.constraints.Positive;

public class AmountRequest {

    @Positive
    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
