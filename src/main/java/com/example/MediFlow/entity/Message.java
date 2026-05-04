package com.example.MediFlow.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String status;

    @ManyToOne
    private Appointment appointment;
}