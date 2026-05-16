package com.example.MediFlow.entity;

import com.example.MediFlow.entity.enums.ConsultationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "consultations")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    private String date;
    private String symptomsStartDate;
        @Enumerated(EnumType.STRING)
        private ConsultationStatus status;

        private String reason;
        private String symptoms;
        private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String notes;

        private Double temperature;
        private Integer heartRate;
    private String pain_level;
    private String allergies;
        private String bloodPressure;
    private String medications;
    @JsonIgnore
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    @JsonIgnore
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<ConsultationMessage> messages;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    }

