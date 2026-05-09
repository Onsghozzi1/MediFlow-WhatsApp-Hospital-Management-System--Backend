package com.example.MediFlow.services;

import com.example.MediFlow.Dtos.Patients.*;

import java.util.List;

public interface IPatientService {
    PatientDTO create_Patient(PatientDTO patientDTO);
    public PatientResponseDto getPatientPagination(int pageNo, int pageSize, String sortBy, String sortDir, PatientFilter filter);
    PatientDTO updatePatient(Long id_Patient, PatientDTO Patient);
    public void changeDeleteStatus(Long id);
    public List<Patient_AppointmentDto> getAllPatients( Long appointmentId);
    List_attributs_patients  etListPatients();



}
