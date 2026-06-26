package com.mediacity.exception;

public class MemberSuspendedException extends RuntimeException {

    public MemberSuspendedException(String memberId) {
        super("L'adhérent " + memberId + " est suspendu");
    }
}
