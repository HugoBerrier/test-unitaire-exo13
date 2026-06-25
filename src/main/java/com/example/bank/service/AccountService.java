package com.example.bank.service;

import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.model.Account;

import java.util.List;

public interface AccountService {

    Account createAccount(CreateAccountRequest request);

    Account getAccount(String number);

    List<Account> getAllAccounts();

    Account deposit(String number, double amount);

    Account withdraw(String number, double amount);

    void transfer(String fromNumber, String toNumber, double amount);
}
