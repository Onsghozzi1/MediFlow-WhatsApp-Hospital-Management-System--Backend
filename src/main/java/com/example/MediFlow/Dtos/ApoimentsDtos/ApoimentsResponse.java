package com.example.MediFlow.Dtos.ApoimentsDtos;

import com.example.MediFlow.Dtos.Patients.PatientDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApoimentsResponse {
    private List<AppoimentsDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
