package com.example.MediFlow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomError {
    private int status;
    private String error;
    private String message;
    private long timestamp;
}
