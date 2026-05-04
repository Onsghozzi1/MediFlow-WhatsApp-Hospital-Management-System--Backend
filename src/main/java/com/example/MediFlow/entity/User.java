package com.example.MediFlow.entity;

import com.example.MediFlow.entity.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "users") // ✅ avoid reserved word "user"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Boolean isValidated;
    private Boolean isDeleted;
    private Long tokenToValidate;
    private Long tokenToForgotPassword;

    @Lob
    @Basic(fetch = FetchType.LAZY) // Add this for lazy loading
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    private LocalDateTime tokenToForgotPasswordCreationDate;
    private LocalDateTime validateCodeCreationDate;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    private Boolean firstTimeLogin;
    private Roles roleTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hospital hospital;
}