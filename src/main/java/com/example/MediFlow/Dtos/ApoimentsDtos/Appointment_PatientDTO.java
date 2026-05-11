package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.entity.enums.AppointmentType;
import com.example.MediFlow.entity.enums.Gender;
import com.example.MediFlow.entity.enums.Priority;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment_PatientDTO {
    private String fullName;
    private String phone;
    private String email;

    private LocalDateTime appointmentDate;
    private String timeSlot;
    @Enumerated(EnumType.STRING)
    private AppointmentType consultMode;
    @Enumerated(EnumType.STRING)
    private Priority reason;

    private Gender gender;
    private LocalDate birthDate;
}
