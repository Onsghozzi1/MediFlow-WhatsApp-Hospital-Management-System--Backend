package com.example.MediFlow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String specialty;
    private String phone;
    private String schedule;
    // disponibilité du docteur
    private boolean isAvailable;

    // charge de travail (nb de patients / jour)
    private int workload;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}