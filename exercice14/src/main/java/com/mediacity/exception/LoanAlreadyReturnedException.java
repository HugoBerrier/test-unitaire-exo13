package com.mediacity.exception;

public class LoanAlreadyReturnedException extends RuntimeException {

    public LoanAlreadyReturnedException(String loanId) {
        super("Le prêt " + loanId + " est déjà retourné");
    }
}
