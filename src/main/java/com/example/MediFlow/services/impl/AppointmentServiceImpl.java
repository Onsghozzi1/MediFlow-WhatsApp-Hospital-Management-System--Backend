package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.*;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Doctor;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.exception.AppointmentException;
import com.example.MediFlow.exception.PatientAlreadyExistsException;
import com.example.MediFlow.mapper.AppoimentMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.PatientRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IAppoimentQuery;
import com.example.MediFlow.services.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentServiceImpl implements IAppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppoimentMapper appoimentMapper;
    @Autowired
    private IAppoimentQuery iAppoimentQuery;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    private static final String PREFIX = "MR-";
    private static final SecureRandom random = new SecureRandom();


    @Transactional
    @Override
    public AppoimentsDto create(AppoimentsDto dto) {

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        User doctor = userRepository.findByEmail(dto.getDoctorEmail())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStatus(dto.getStatus());
        appointment.setAppointment_Type(dto.getAppointmentType());
        appointment.setPriority(dto.getPriority());
        appointment.setReason(dto.getReason());
        appointment.setNotes(dto.getNotes());
        appointment.setIs_delete(false);
        Appointment saved = appointmentRepository.save(appointment);

        return appoimentMapper.mapTo_appoiment_DTO(saved);
    }    @Override
    public ApoimentsResponse getAppointmentPagination(int pageNo, int pageSize, String sortBy, String sortDir, ApoimentFilter filter) {
        return iAppoimentQuery.getAppointmentPagination( pageNo, pageSize, sortBy, sortDir, filter);
    }
    public void changeDeleteStatus(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setIs_delete(true);
        appointmentRepository.save(appointment);
    }
    @Transactional
    @Override
    public AppoimentsDto update(Long id, AppoimentsDto dto) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        User doctor = userRepository.findByEmail(dto.getDoctorEmail())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // update fields only (no recreate)
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointment_Type(dto.getAppointmentType());
        appointment.setStatus(dto.getStatus());
        appointment.setPriority(dto.getPriority());
        appointment.setReason(dto.getReason());
        appointment.setNotes(dto.getNotes());

        Appointment saved = appointmentRepository.save(appointment);

        return appoimentMapper.mapTo_appoiment_DTO(saved);
    }

    @Override
    public List<Appointment_calendar> getAllAppointments() {

        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream()
                .map(this::mapToCalendar)
                .toList();
    }

    @Override
    public List<AllPatients> getAllAppointmentsPatient() {
        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream()
                .map(app -> AllPatients.builder()
                        .patientId(app.getPatient().getId())
                        .fullName(app.getPatient().getFullName() )

                        .build())
                .toList();
    }

    private Appointment_calendar mapToCalendar(Appointment a) {

        Appointment_calendar dto = new Appointment_calendar();

        dto.setId(a.getId());
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setPatientId(a.getPatient().getId());
        dto.setPatientName(a.getPatient().getFullName());
        dto.setStatus(a.getStatus());
        dto.setPriority(a.getPriority());

        return dto;
    }

    public Appointment createAppointment2(
            Long doctorId,
            String patientName,
            LocalDate date,
            LocalTime startTime
    ) {

        User doctor = userRepository.getReferenceById(doctorId);

        LocalDateTime start = LocalDateTime.of(date, startTime);

        LocalDateTime end = start.plusMinutes(30);

        boolean conflict =
                appointmentRepository.existsConflict(
                        doctorId,
                        start,
                        end
                );

        if (conflict) {
            throw new RuntimeException(
                    "This slot is already reserved"
            );
        }
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setStatus(Status.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment moveAppointment(Long id, MoveAppointmentRequest request) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setEndTime(request.getEndDate());

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment_PatientDTO create_Appointment_patient(Appointment_PatientDTO dto) {

        if (dto == null) {
            throw new RuntimeException("DTO is null");
        }

        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            throw new PatientAlreadyExistsException("Phone number is required");
        }

        // ============================
        // CHECK PHONE
        // ============================
        if (patientRepository.existsByPhone(dto.getPhone())) {
            throw new PatientAlreadyExistsException("Phone number already exists");
        }

        // ============================
        // CREATE PATIENT
        // ============================
        Patient patient = new Patient();
        patient.setFullName(dto.getFullName());
        patient.setPhone(dto.getPhone());
        patient.setWhatsappNumber(dto.getPhone());
        patient.setIsDelete(false);
        patient.setCreate_date_time(LocalDateTime.now());
        patient.setMedical_Record_ID(generateMedicalRecordId());

        patient = patientRepository.save(patient);

        // ============================
        // BLOCK APPOINTMENT CREATION
        // ============================
        boolean appointmentExists =
                appointmentRepository.existsAppointment(
                        dto.getAppointmentDate()
                );

        if (appointmentExists) {
            // 🔴 BLOQUE ICI COMPLETEMENT
            throw new AppointmentException(
                    "Appointment already exists for another patient at the same date"
            );
        }

        // ============================
        // CREATE APPOINTMENT
        // ============================
        Appointment appointment = new Appointment();

        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointment_Type(dto.getConsultMode());
        appointment.setIs_delete(false);
        appointment.setPatient(patient);
        appointment.setPriority(dto.getReason());
        appointmentRepository.save(appointment);

        return dto;
    }
    // ===================================
    // GENERATE MEDICAL RECORD ID
    // ===================================



    public String generateMedicalRecordId() {
        int number = 100000 + random.nextInt(900000); // 6 digits
        return PREFIX + number;
    }
    // =========================
    // GENERATE AVAILABLE SLOTS
    // =========================

    public List<LocalTime> getAvailableSlots(
            Long doctorId,
            LocalDate date
    ) {

        LocalTime startDay = LocalTime.of(9, 0);
        LocalTime endDay = LocalTime.of(17, 0);

        int duration = 30;

        List<LocalTime> allSlots = new ArrayList<>();

        LocalTime current = startDay;

        while (
                current.plusMinutes(duration).isBefore(endDay)
                        || current.plusMinutes(duration).equals(endDay)
        ) {

            allSlots.add(current);

            current = current.plusMinutes(duration);
        }

        // appointments réservés

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndStartTimeBetween(
                        doctorId,
                        date.atStartOfDay(),
                        date.atTime(23, 59)
                );

        List<LocalTime> reserved =
                appointments.stream()
                        .map(a -> a.getStartTime().toLocalTime())
                        .toList();

        return allSlots.stream()
                .filter(slot -> !reserved.contains(slot))
                .toList();
    }

}
