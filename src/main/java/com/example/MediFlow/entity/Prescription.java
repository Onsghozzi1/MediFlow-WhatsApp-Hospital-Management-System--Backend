package com.example.MediFlow.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Prescription {

    @Id
    @GeneratedValue
    private Long id;

    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;
}