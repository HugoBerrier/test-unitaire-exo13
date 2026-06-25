package com.example.bank.dto;

import com.example.bank.model.Account;

public class AccountResponse {

    private final String number;
    private final String holder;
    private final double balance;

    public AccountResponse(String number, String holder, double balance) {
        this.number = number;
        this.holder = holder;
        this.balance = balance;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getNumber(), account.getHolder(), account.getBalance());
    }

    public String getNumber() {
        return number;
    }

    public String getHolder() {
        return holder;
    }

    public double getBalance() {
        return balance;
    }
}
