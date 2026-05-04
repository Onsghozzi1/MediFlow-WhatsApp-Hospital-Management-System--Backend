package com.example.MediFlow.Dtos.user_dto;

import com.example.MediFlow.entity.enums.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Instant createdAt;
    private Boolean firstTimeLogin;
    @Column(length = 10000000)
    @Lob
    private byte[] profilePicture;
    private Boolean isValidated;
    private Roles roleTypes;
    private String phone;


}
