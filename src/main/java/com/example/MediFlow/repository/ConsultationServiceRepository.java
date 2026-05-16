package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Consultation;
import com.example.MediFlow.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface ConsultationServiceRepository extends JpaRepository<Consultation, Long> { }

