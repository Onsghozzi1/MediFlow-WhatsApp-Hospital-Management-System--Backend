package com.example.MediFlow.exception;

public class InvalidBirthDateException extends RuntimeException {
    public InvalidBirthDateException(String message) {
        super(message);
    }
}