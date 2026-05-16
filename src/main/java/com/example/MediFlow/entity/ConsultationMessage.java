package com.example.MediFlow.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ConsultationMessage {

    @Id
    @GeneratedValue
    private Long id;

    private String sender; // DOCTOR / PATIENT
    private String message;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;
}