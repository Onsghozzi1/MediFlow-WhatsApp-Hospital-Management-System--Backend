package com.example.MediFlow.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String code;
    private String message;
    private Object data;
}