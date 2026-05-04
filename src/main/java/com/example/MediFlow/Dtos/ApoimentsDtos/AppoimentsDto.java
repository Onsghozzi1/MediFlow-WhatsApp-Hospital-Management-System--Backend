package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.entity.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppoimentsDto {

    @NotNull(message = "DateTime is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime dateTime;

    @NotNull(message = "Patient is required")
    private Long patientId;

    @NotNull(message = "Doctor is required")
    private Long doctorId;

    @NotNull(message = "Hospital is required")
    private Long hospitalId;

    @Enumerated(EnumType.STRING)
    private Status status;
}
