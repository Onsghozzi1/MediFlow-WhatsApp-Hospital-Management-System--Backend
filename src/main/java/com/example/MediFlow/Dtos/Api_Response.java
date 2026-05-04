package com.example.MediFlow.Dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Api_Response<T> {

    private boolean success;

    private String message;

    private T data;
}