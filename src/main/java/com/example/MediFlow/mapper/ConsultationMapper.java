package com.example.MediFlow.mapper;

import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.consultation.ConsultationDTO;
import com.example.MediFlow.entity.Consultation;
import com.example.MediFlow.entity.Patient;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ConsultationMapper {
    @Autowired
    private ModelMapper modelMapper;

    public ConsultationDTO mapToConsultationDTO(Consultation consultation) {
        return modelMapper.map(consultation, ConsultationDTO.class);
    }

    public Consultation mapToConsultation(ConsultationDTO consultationDTO) {
        return modelMapper.map(consultationDTO, Consultation.class);
    }

}
