package com.example.MediFlow.Dtos.Patients;

import com.example.MediFlow.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDTO {

    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[0-9+\\-\\s]{8,20}$",
            message = "Phone number format is invalid"
    )
    private String phone;

    @Pattern(
            regexp = "^[0-9+\\-\\s]{8,20}$",
            message = "WhatsApp number format is invalid"
    )
    private String whatsappNumber;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @Size(max = 2000, message = "Medical history must not exceed 2000 characters")
    private String medicalHistory;

    private Long hospitalId;
    private String hospitalName;
    @NotNull(message = "Gender is required")
    private Gender gender;
    private Integer age;
    private String address;
    private String medical_Record_ID;
    private Boolean is_delete;

}