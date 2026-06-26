package com.mediacity.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String memberId) {
        super("Adhérent introuvable : " + memberId);
    }
}
