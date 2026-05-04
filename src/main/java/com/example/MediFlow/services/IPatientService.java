package com.example.MediFlow.services;

import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;

public interface IPatientService {
    PatientDTO create_Patient(PatientDTO patientDTO);
    public PatientResponseDto getPatientPagination(int pageNo, int pageSize, String sortBy, String sortDir, PatientFilter filter);
    PatientDTO updatePatient(Long id_Patient, PatientDTO Patient);
    public void changeDeleteStatus(Long id);

}
