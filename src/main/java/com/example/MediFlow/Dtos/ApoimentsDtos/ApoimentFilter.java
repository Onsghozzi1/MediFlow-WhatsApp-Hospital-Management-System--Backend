package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.entity.enums.AppointmentType;
import com.example.MediFlow.entity.enums.Priority;
import com.example.MediFlow.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class ApoimentFilter {
    private Long id;
    private String patient_name;
    private LocalDateTime AppointmentDate;
    private Priority priority;
    private Status status;
    private AppointmentType appointment_Type;

}
