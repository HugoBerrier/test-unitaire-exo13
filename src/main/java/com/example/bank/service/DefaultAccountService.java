package com.example.bank.service;

import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.exception.AccountAlreadyExistsException;
import com.example.bank.exception.AccountNotFoundException;
import com.example.bank.exception.InsufficientFundsException;
import com.example.bank.exception.InvalidAmountException;
import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;

    public DefaultAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        if (accountRepository.existsByNumber(request.getNumber())) {
            throw new AccountAlreadyExistsException(request.getNumber());
        }

        Account account = new Account();
        account.setNumber(request.getNumber());
        account.setHolder(request.getHolder());
        account.setBalance(0);
        return accountRepository.save(account);
    }

    @Override
    public Account getAccount(String number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account deposit(String number, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException();
        }

        Account account = getAccount(number);
        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account);
    }

    @Override
    public Account withdraw(String number, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException();
        }

        Account account = getAccount(number);
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException();
        }

        account.setBalance(account.getBalance() - amount);
        return accountRepository.save(account);
    }

    @Override
    public void transfer(String fromNumber, String toNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException();
        }

        Account fromAccount = accountRepository.findByNumber(fromNumber)
                .orElseThrow(() -> new AccountNotFoundException(fromNumber));
        Account toAccount = accountRepository.findByNumber(toNumber)
                .orElseThrow(() -> new AccountNotFoundException(toNumber));

        if (fromAccount.getBalance() < amount) {
            throw new InsufficientFundsException();
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}
