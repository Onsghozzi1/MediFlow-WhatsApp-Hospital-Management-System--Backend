package com.example.MediFlow.Dtos.ApoimentsDtos;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MoveAppointmentRequest {
    private LocalDateTime appointmentDate;
    private LocalDateTime endDate;
}
