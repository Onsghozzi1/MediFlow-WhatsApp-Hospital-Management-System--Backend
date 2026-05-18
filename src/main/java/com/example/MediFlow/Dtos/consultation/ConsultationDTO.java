package com.example.MediFlow.Dtos.consultation;

import com.example.MediFlow.Dtos.ApoimentsDtos.Appoi_dto;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.Doctor.DoctorDto;
import com.example.MediFlow.Dtos.Doctor.DoctorDtos_consultation;
import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.entity.enums.ConsultationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultationDTO {

    public Long id;
    public Patient_consultation_Dtos patient;
    public DoctorDtos_consultation doctor;
    public Appoi_dto appointment;
    public ConsultationStatus status;
    public String reason;
    public String symptoms;
    public String diagnosis;
    public String notes;
    private List<PrescriptionDTO> dto;


}