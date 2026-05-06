package com.example.MediFlow.entity;

import com.example.MediFlow.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String phone;
    private String whatsappNumber;
    private LocalDate birthDate;

    @Column(name = "medical_record_id", unique = true, nullable = false, updatable = false)
    private String medical_Record_ID;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String address;
    private Integer age;
    @Column(length = 2000)
    private String medicalHistory;
    @ManyToOne(fetch = FetchType.LAZY)
    private Hospital hospital;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime create_date_time ;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime update_date_time ;
    @Column(name = "is_delete")
    private Boolean isDelete;
}