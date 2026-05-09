package com.example.MediFlow.Dtos.Patients;

import com.example.MediFlow.entity.enums.Gender;
import lombok.Data;

import java.util.List;
@Data
public class List_attributs_patients {
    private List<String> medicalRecordIds;
    private List<String> full_name;
    private List<String> phone;
    private List<String> address;
}
