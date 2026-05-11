package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecialty(String specialty);
    Optional<Doctor> findByUserId(Long userId);
}