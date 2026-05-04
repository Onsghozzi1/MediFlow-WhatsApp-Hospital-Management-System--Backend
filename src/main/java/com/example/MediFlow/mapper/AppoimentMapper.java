package com.example.MediFlow.mapper;

import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Patient;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
@Getter
public class AppoimentMapper {

        @Autowired
        private ModelMapper modelMapper;

        public AppoimentsDto mapTo_appoiment_DTO(Appointment appointment) {
            return modelMapper.map(appointment, AppoimentsDto.class);
        }
        public Appointment mapToappointment(AppoimentsDto appoimentsDto) {
            return modelMapper.map(appoimentsDto, Appointment.class);
        }



}
