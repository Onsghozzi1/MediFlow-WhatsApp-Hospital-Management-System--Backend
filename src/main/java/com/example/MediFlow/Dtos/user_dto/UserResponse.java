package com.example.MediFlow.Dtos.user_dto;

import com.example.MediFlow.entity.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private byte[] profilePicture;
    private Boolean firstTimeLogin;
    private Roles roleTypes;
}