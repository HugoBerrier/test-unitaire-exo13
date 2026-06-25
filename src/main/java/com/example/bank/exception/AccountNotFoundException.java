package com.example.bank.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String number) {
        super("Account not found with number: " + number);
    }
}
