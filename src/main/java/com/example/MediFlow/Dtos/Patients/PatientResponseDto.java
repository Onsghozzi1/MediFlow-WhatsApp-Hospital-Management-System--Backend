package com.example.MediFlow.Dtos.Patients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDto {
    private List<PatientDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private long totalMale;
    private long totalFemale;
}
