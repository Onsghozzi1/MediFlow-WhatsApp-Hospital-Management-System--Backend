package com.example.MediFlow.entity;

import com.example.MediFlow.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Appointment {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDateTime dateTime;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private User doctor;

    @ManyToOne
    private Hospital hospital;

}