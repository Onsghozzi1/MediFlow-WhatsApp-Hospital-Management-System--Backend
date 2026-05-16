package com.example.MediFlow.Dtos.consultation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrescriptionDTO {
private Long id;

        private String medicineName;

        private String dosage;

        private String frequency;

        private String duration;

        private String instructions;

    }

