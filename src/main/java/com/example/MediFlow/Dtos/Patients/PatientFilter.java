package com.example.MediFlow.Dtos.Patients;

import com.example.MediFlow.entity.enums.Gender;
import lombok.Data;

import java.util.List;

@Data
public class PatientFilter {
    private Long id;
    private String medicalRecordIds;
    private String full_name;
    private String phone;
    private String address;
    private Gender gender;

}
