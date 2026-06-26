package com.mediacity.exception;

public class WorkUnavailableException extends RuntimeException {

    public WorkUnavailableException(String workId) {
        super("L'ouvrage " + workId + " est déjà emprunté");
    }
}
