package com.example.MediFlow.Dtos.user_dto;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserUpdateResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String roleTypes;
    private String profilePicture; // Base64 encoded string
}
