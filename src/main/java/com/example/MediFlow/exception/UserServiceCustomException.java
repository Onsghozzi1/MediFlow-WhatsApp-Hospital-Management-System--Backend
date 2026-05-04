package com.example.MediFlow.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class UserServiceCustomException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus errorStatus;

    public UserServiceCustomException(String message, String errorCode, HttpStatus errorStatus) {
        super(message);
        this.errorCode = errorCode;
        this.errorStatus = errorStatus;
    }

    public UserServiceCustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.errorStatus = HttpStatus.BAD_REQUEST;
    }
}