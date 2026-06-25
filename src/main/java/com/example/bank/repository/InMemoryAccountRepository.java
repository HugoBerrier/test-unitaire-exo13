package com.example.bank.repository;

import com.example.bank.model.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> storage = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        storage.put(account.getNumber(), account);
        return account;
    }

    @Override
    public Optional<Account> findByNumber(String number) {
        return Optional.ofNullable(storage.get(number));
    }

    @Override
    public boolean existsByNumber(String number) {
        return storage.containsKey(number);
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(storage.values());
    }
}
