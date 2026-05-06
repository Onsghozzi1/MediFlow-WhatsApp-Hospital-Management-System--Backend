package com.example.MediFlow.entity;

import com.example.MediFlow.entity.enums.AppointmentType;
import com.example.MediFlow.entity.enums.Priority;
import com.example.MediFlow.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Appointment {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate AppointmentDate;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private Patient patient;
    @ManyToOne
    private User doctor;
    @ManyToOne
    private Hospital hospital;
    @Enumerated(EnumType.STRING)
    private AppointmentType appointment_Type;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private String reason;
    private String notes;
    private Boolean is_delete;




}