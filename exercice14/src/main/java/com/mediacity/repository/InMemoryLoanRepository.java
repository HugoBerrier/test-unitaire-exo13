package com.mediacity.repository;

import com.mediacity.model.Loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryLoanRepository implements LoanRepository {

    private final Map<String, Loan> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Loan save(Loan loan) {
        if (loan.getId() == null) {
            loan.setId(String.valueOf(idGenerator.getAndIncrement()));
        }
        storage.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Loan> findActiveByWorkId(String workId) {
        return storage.values().stream()
                .filter(loan -> workId.equals(loan.getWorkId()) && loan.isActive())
                .findFirst();
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
