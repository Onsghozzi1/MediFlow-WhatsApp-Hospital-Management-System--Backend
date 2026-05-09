package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM public.appointment a
        JOIN patient p ON a.patient_id = p.id
        AND a.appointment_date = :appointmentDate
    )
""", nativeQuery = true)
    boolean existsAppointment(
            @Param("appointmentDate") LocalDateTime appointmentDate
    );

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

    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    AND (
        :startTime < a.endTime
        AND :endTime > a.startTime
    )
    """)
    boolean existsConflict(
            @Param("doctorId") Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<Appointment> findByDoctorIdAndStartTimeBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );
}