package com.example.MediFlow.Dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Password_dto {
    private String  current_password;
    private String new_password;
    private String confirm_password;
}
