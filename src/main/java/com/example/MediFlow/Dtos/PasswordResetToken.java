package com.example.MediFlow.Dtos;

import com.example.MediFlow.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private LocalDateTime expirationDate;

    // getters/setters
}
