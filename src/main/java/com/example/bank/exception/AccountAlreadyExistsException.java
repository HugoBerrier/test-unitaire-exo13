package com.example.bank.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String number) {
        super("Account already exists with number: " + number);
    }
}
