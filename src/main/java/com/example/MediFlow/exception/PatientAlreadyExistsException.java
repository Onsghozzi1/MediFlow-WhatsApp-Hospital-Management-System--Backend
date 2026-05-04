package com.example.MediFlow.exception;
public class PatientAlreadyExistsException extends RuntimeException {
    public PatientAlreadyExistsException(String message) {
        super(message);
    }
}
