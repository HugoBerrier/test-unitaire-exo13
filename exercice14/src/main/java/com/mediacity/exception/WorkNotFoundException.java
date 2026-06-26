package com.mediacity.exception;

public class WorkNotFoundException extends RuntimeException {

    public WorkNotFoundException(String workId) {
        super("Ouvrage introuvable : " + workId);
    }
}
