package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.entity.enums.Priority;
import com.example.MediFlow.entity.enums.Status;
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
public class Appointment_calendar {
    private LocalDateTime appointmentDate;
    private Status status;
    private String patientName ;
    private Long patientId ;
    private Long id ;
    private Priority priority;

}
