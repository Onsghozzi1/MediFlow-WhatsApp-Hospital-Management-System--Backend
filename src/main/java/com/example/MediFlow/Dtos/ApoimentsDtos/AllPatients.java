package com.example.MediFlow.Dtos.ApoimentsDtos;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AllPatients {
    private Long patientId;
        private String fullName;

}
