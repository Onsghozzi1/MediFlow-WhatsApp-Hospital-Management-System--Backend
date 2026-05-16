package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.entity.enums.AppointmentType;
import com.example.MediFlow.entity.enums.Priority;
import com.example.MediFlow.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class Appoi_dto {
        private Long id ;
        private LocalDateTime appointmentDate;
        private AppointmentType appointmentType;
        private String doctor_name;
        private String patient_name;
        private Priority priority;
        private Status status;
        private Long patientId;
        private String reason;
        private String notes;
        private String whatsApp_number;
    }

