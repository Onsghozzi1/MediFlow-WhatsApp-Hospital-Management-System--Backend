package com.example.MediFlow.exception;
public class AccountNotValidatedException extends RuntimeException {
    public AccountNotValidatedException(String message) {
        super(message);
    }
}
