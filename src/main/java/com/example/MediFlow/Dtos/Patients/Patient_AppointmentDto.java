package com.example.MediFlow.Dtos.Patients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Patient_AppointmentDto {
    private Long patientId;
    private String fullName;
}
