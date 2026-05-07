package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    @Query(value = """
 SELECT p.id
             FROM patient p
             WHERE NOT EXISTS (
                 SELECT 1 FROM appointment a WHERE a.patient_id = p.id
             )
""",nativeQuery = true)
    Set<Long>  findalldPatientIds();


    @Query(value = """
    SELECT p.id
                 FROM patient p
                 LEFT JOIN appointment a
                     ON a.patient_id = p.id
                 WHERE a.patient_id IS NULL
                    OR a.id = :appointmentId
                 ORDER BY p.id
""", nativeQuery = true)
    Set<Long> findAllPatient(@Param("appointmentId") Long appointmentId);
    @Query("""
SELECT a FROM Appointment a
JOIN a.patient p
WHERE p.id = :patientId
""")
    Appointment findByPatient(@Param("patientId") Long patientId);
}