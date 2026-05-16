package com.example.MediFlow.repository;

import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
/*
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
    );*/

    List<Appointment> findByDoctorIdAndStartTimeBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByDoctorId(
            Long doctorId
    );

    @Query("""
SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
FROM Appointment a
WHERE a.doctor.id = :doctorId
AND (
    :start < a.endTime
    AND
    :end > a.startTime
)
""")
    boolean isDoctorBusy(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
           SELECT a
           FROM Appointment a
           WHERE a.AppointmentDate BETWEEN :start AND :end
           """)
    List<Appointment> findAppointmentsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


  /*  @Query(value = """
SELECT *
FROM appointment a
WHERE a.doctor_id = :doctorId
AND DATE(a.appointment_date) = CURRENT_DATE
AND a.appointment_date >= NOW()
ORDER BY a.appointment_date ASC
""", nativeQuery = true)
    List<Appointment> findTodayUpcomingAppointments(
            @Param("doctorId") Long doctorId
    );
*/
  @Query(value = """
SELECT *
FROM appointment a
WHERE a.doctor_id = :doctorId
AND DATE(a.appointment_date) = CURRENT_DATE
AND CURRENT_TIMESTAMP BETWEEN a.start_time AND a.end_time
ORDER BY a.start_time ASC
""", nativeQuery = true)
  List<Appointment> findTodayActiveAppointments(
          @Param("doctorId") Long doctorId
  );

    @Query("""
SELECT a
FROM Appointment a
WHERE a.doctor.id = :doctorId
AND a.startTime > :now
ORDER BY a.startTime ASC
LIMIT 1
""")
    Optional<Appointment> findNextAppointment(
            Long doctorId,
            LocalDateTime now
    );
    @Query("""
    SELECT DISTINCT a
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    AND a.is_delete = false
""")
    List<Appointment> findPatientsByDoctorId(Long doctorId);
}