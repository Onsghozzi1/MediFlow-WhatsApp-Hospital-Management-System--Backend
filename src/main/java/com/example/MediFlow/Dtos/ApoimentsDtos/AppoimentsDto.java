package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.entity.enums.AppointmentType;
import com.example.MediFlow.entity.enums.Priority;
import com.example.MediFlow.entity.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AppoimentsDto {
    @NotNull(message = "DateTime is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Appointment type is required")
    private AppointmentType appointmentType;

    @NotBlank(message = "Doctor is required")
    private String doctorEmail;

    private String notes;

    @NotNull(message = "Patient is required")
    private Long patientId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotBlank(message = "Reason is required")
    private String reason;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    private Status status;
}
