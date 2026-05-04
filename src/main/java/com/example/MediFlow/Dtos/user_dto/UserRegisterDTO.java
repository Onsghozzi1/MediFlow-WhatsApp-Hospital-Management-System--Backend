package com.example.MediFlow.Dtos.user_dto;

import jakarta.validation.constraints.*;
import com.example.MediFlow.entity.enums.Roles;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDTO {

    // =========================
    // NAME
    // =========================
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    // =========================
    // EMAIL
    // =========================
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email is too long")
    private String email;

    // =========================
    // PASSWORD (STRONG 🔥)
    // =========================
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;

    // =========================
    // PHONE
    // =========================
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{8,15}$",
            message = "Phone number must contain only digits (8-15)"
    )
    private String phoneNumber;

    // =========================
    // ROLE
    // =========================
    @NotNull(message = "Role is required")
    private Roles roleTypes;

    // =========================
    // PROFILE PICTURE
    // =========================
    private byte[] profilePicture;
}