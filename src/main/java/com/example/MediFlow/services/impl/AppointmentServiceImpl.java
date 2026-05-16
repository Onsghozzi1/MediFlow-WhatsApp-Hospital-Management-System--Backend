package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.*;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Doctor;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.exception.*;
import com.example.MediFlow.mapper.AppoimentMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.DoctorRepository;
import com.example.MediFlow.repository.PatientRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IAppoimentQuery;
import com.example.MediFlow.services.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

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
    @Autowired
    private DoctorRepository doctorRepository;
    private static final String PREFIX = "MR-";
    private static final SecureRandom random = new SecureRandom();

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceCustomException("User not found","error"));
    }

    @Transactional
    @Override
    public AppoimentsDto create(AppoimentsDto dto) {

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new PatientAlreadyExistsException("Patient not found"));

        User user = getCurrentUser();
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new DoctorException("Doctor not found"));
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getAppointmentDate());
        appointment.setEndTime(dto.getAppointmentDate().plusMinutes(30));
        appointment.setStatus(Status.SCHEDULED);
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
                .orElseThrow(() -> new AppointmentException("Appointment not found"));
        appointment.setIs_delete(true);
        appointmentRepository.save(appointment);
    }
    @Transactional
    @Override
    public AppoimentsDto update(Long id, AppoimentsDto dto) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentException("Appointment not found"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new PatientAlreadyExistsException("Patient not found"));
        User user = getCurrentUser();
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new DoctorException("Doctor not found"));

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

        User user = getCurrentUser();
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new DoctorException("Doctor not found"));
        List<Appointment> appointments =
                appointmentRepository.findByDoctorId(doctor.getId());

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

    @Override
    public Appointment moveAppointment(Long id, MoveAppointmentRequest request) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentException("Appointment not found"));

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setEndTime(request.getEndDate());

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment_PatientDTO create_Appointment_patient(Appointment_PatientDTO dto) {

        if (dto == null) {
            throw new RuntimeException("DTO is null");
        }
        if (dto.getBirthDate() == null || !dto.getBirthDate().isBefore(LocalDate.now())) {
            throw new InvalidBirthDateException("Birth date must be strictly in the past");
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
        patient.setGender(dto.getGender());

        patient.setBirthDate(dto.getBirthDate());



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

        LocalDateTime appointmentDate = dto.getAppointmentDate();

        if (appointmentDate == null) {
            throw new AppointmentException("Appointment date is required");
        }

        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new AppointmentException("Appointment date must be in the future");
        }
        // =========================
        // APPOINTMENT TIME SLOT
        // =========================
        LocalDateTime start = dto.getAppointmentDate();
        LocalDateTime end = start.plusMinutes(30);

// ===========================
// 1. DOCTORS AVAILABLE + FREE SLOT
// ===========================
        List<Doctor> availableDoctors = doctorRepository.findAll().stream()
                .filter(Doctor::isAvailable)
                .filter(d -> !appointmentRepository.isDoctorBusy(d.getId(), start, end))
                .toList();

// ===========================
// 2. FALLBACK: ALL FREE DOCTORS (even if not available)
// ===========================
        List<Doctor> fallbackDoctors = doctorRepository.findAll().stream()
                .filter(d -> !appointmentRepository.isDoctorBusy(d.getId(), start, end))
                .toList();

// ===========================
// 3. CHOOSE LIST
// ===========================
        List<Doctor> candidates = !availableDoctors.isEmpty()
                ? availableDoctors
                : fallbackDoctors;

// ===========================
// 4. IF NO DOCTOR → EXCEPTION
// ===========================
        if (candidates.isEmpty()) {
            throw new DoctorException("No doctor available for this time slot");
        }

// ===========================
// 5. SELECT LOWEST WORKLOAD
// ===========================
        Doctor selectedDoctor = candidates.stream()
                .min(Comparator.comparingInt(Doctor::getWorkload))
                .orElseThrow();
        //
//        // ============================
//        // CREATE APPOINTMENT
//        // ============================
//        Doctor selectedDoctor;
//
//        List<Doctor> doctors = doctorRepository.findAll();
//
//// 1️⃣ essayer les doctors disponibles
//        Optional<Doctor> availableDoctor = doctors.stream()
//                .filter(Doctor::isAvailable)
//                .min(Comparator.comparingInt(Doctor::getWorkload));
//
//// 2️⃣ si aucun available → fallback
//        if (availableDoctor.isPresent()) {
//
//            selectedDoctor = availableDoctor.get();
//
//        } else {
//
//            // fallback: choisir le moins chargé même s'il n'est pas available
//            selectedDoctor = doctors.stream()
//                    .min(Comparator.comparingInt(Doctor::getWorkload))
//                    .orElseThrow();
//        }        selectedDoctor.setAvailable(false);
       selectedDoctor.setWorkload(selectedDoctor.getWorkload() + 1);
doctorRepository.save(selectedDoctor);
        patient = patientRepository.save(patient);

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointment_Type(dto.getConsultMode());
        appointment.setStartTime(dto.getAppointmentDate());
        appointment.setEndTime(dto.getAppointmentDate().plusMinutes(30));
        appointment.setIs_delete(false);
        appointment.setPatient(patient);
        appointment.setPriority(dto.getReason());
        appointment.setStatus(Status.SCHEDULED);
       appointment.setDoctor(selectedDoctor);
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
    public List<String> getBookedSlots(LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments =
                appointmentRepository.findAppointmentsBetween(start, end);
        return appointments.stream()
                .map(a -> a.getAppointmentDate()
                        .atZone(ZoneId.of("Africa/Tunis"))
                        .toLocalTime()
                )
                .map(time -> String.format("%02d:%02d", time.getHour(), time.getMinute()))
                .toList();
    }
}
