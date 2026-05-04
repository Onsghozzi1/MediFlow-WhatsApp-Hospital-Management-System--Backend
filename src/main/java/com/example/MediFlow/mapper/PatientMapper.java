package com.example.MediFlow.mapper;

import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.entity.Patient;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PatientMapper {
    @Autowired
    private ModelMapper modelMapper;

    public PatientDTO mapToPatientDTO(Patient patient) {
        return modelMapper.map(patient, PatientDTO.class);
    }

    public Patient mapToPatient(PatientDTO patientDTO) {
        return modelMapper.map(patientDTO, Patient.class);
    }

}
