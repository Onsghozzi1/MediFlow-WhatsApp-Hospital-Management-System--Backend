package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByHospitalId(Long hospitalId);
    boolean existsByFullNameAndPhone(String fullName, String phone);

}