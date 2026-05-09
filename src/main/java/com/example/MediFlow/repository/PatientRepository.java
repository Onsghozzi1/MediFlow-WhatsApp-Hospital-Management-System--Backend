package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByHospitalId(Long hospitalId);
    boolean existsByFullNameAndPhone(String fullName, String phone);
    List<Patient> findByIsDeleteFalse();
    Optional<Patient> findByPhone(String phone);
    boolean existsByPhone(String phone);

}