package com.mediacity.repository;

import com.mediacity.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(String id);

    Optional<Loan> findActiveByWorkId(String workId);

    List<Loan> findAll();

    void clear();
}
