package com.mediacity.exception;

public class WorkAvailableException extends RuntimeException {

    public WorkAvailableException(String workId) {
        super("L'ouvrage " + workId + " est disponible, la réservation n'est pas nécessaire");
    }
}
