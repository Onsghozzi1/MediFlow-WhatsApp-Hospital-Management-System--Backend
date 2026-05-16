package com.example.MediFlow.mapper;

import com.example.MediFlow.Dtos.Doctor.DoctorDto;
import com.example.MediFlow.entity.Doctor;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DoctorMapper {
    @Autowired
    private ModelMapper modelMapper;

    public DoctorDto mapToDoctorDTO(Doctor doctor) {
        return modelMapper.map(doctor, DoctorDto.class);
    }

    public Doctor mapToDoctor(DoctorDto doctorDto) {
        return modelMapper.map(doctorDto, Doctor.class);
    }
}
