package com.example.MediFlow.Dtos.consultation;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PatientCardDTO {

    private Long patientId;
    private Long appointmentId;
    private LocalDateTime appointmentDate;
    private LocalDateTime nextAppointmentTime;
    private String doctorName;
    private String full_name_patient;
    private Boolean activeAppointment;

}