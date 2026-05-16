package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PrescriptionRepository  extends JpaRepository<Prescription, Long>{

}
