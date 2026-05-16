package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.Patients.*;
import com.example.MediFlow.Dtos.consultation.PatientCardDTO;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Doctor;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.exception.PatientAlreadyExistsException;
import com.example.MediFlow.exception.UserServiceCustomException;
import com.example.MediFlow.mapper.PatientMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.DoctorRepository;
import com.example.MediFlow.repository.PatientRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IPatientQuery;
import com.example.MediFlow.services.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
public class PatientServiceImpl implements IPatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private IPatientQuery iPatientQuery;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    private static final String PREFIX = "MR-";
    private static final SecureRandom random = new SecureRandom();

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceCustomException("User not found","error"));
    }
    @Override
    public PatientDTO create_Patient(PatientDTO patientDTO) {
        // ✅ Check if patient already exists
        boolean exists = patientRepository
                .existsByFullNameAndPhone(patientDTO.getFullName(), patientDTO.getPhone());
        if (exists) {
            throw new PatientAlreadyExistsException("Patient already exists with same full name and phone");
        }
        User user=getCurrentUser();

        Doctor doctor1 = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Convert DTO to Entity
        Patient entity = patientMapper.mapToPatient(patientDTO);
        entity.setCreate_date_time(LocalDateTime.now());
        entity.setMedical_Record_ID(generateMedicalRecordId());
        entity.setAge(Period.between(patientDTO.getBirthDate(), LocalDate.now()).getYears());
        entity.setAddress(patientDTO.getAddress());
        entity.setDoctor(doctor1);
                entity.setIsDelete(false);
        // Sauvegarder en base
        Patient savedEntity = patientRepository.save(entity);
        return mapToDto(savedEntity);
    }

    public String generateMedicalRecordId() {
        int number = 100000 + random.nextInt(900000); // 6 digits
        return PREFIX + number;
    }
    @Override
    public PatientResponseDto getPatientPagination(int pageNo, int pageSize, String sortBy, String sortDir, PatientFilter filter) {
        return iPatientQuery.getPatientPagination( pageNo,  pageSize,  sortBy,  sortDir,  filter);
    }
    @Override
    public PatientDTO updatePatient(Long id_Patient, PatientDTO patientDTO) {

        Patient patient = patientRepository.findById(id_Patient)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id_Patient));

        User user=getCurrentUser();
        Doctor doctor1 = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        // update timestamp
        patient.setUpdate_date_time(LocalDateTime.now());
        patient.setFullName(patientDTO.getFullName());
        patient.setPhone(patientDTO.getPhone());
        patient.setWhatsappNumber(patientDTO.getWhatsappNumber());
        patient.setAddress(patientDTO.getAddress());
        patient.setGender(patientDTO.getGender());
        patient.setBirthDate(patientDTO.getBirthDate());
         patient.setDoctor(doctor1);
        // recalcul age
        if (patientDTO.getBirthDate() != null) {
            patient.setAge(
                    Period.between(patientDTO.getBirthDate(), LocalDate.now()).getYears()
            );
        }
        Patient saved = patientRepository.save(patient);
        return mapToDto(saved);
    }


    private PatientDTO mapToDto(Patient user) {
        PatientDTO dto = new PatientDTO();
        dto.setBirthDate(user.getBirthDate());
        dto.setPhone(user.getPhone());
        dto.setFullName(user.getFullName());
        dto.setWhatsappNumber(user.getWhatsappNumber());
        dto.setGender(user.getGender());
        return dto;
    }
    public void changeDeleteStatus(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setIsDelete(true);
        patient.setUpdate_date_time(LocalDateTime.now());
        patientRepository.save(patient);
    }

    @Override
    public List<Patient_AppointmentDto> getAllPatients(Long appointmentId) {
User user =getCurrentUser();
        List<Patient> patients = patientRepository.findByDoctorIdAndIsDeleteFalse(user.getDoctor().getId());

        Set<Long> blockedIds;
        if (appointmentId == null) {
         blockedIds = appointmentRepository.findalldPatientIds();
      } else {
          blockedIds = appointmentRepository.findAllPatient(appointmentId);
     }

        return patients.stream()
                .filter(p -> blockedIds.contains(p.getId()))
                .distinct()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List_attributs_patients etListPatients() {

        List<Patient> patients = patientRepository.findAll();

        List_attributs_patients dto = new List_attributs_patients();

        dto.setMedicalRecordIds(
                patients.stream()
                        .map(Patient::getMedical_Record_ID)
                        .filter(Objects::nonNull)
                        .toList()
        );

        dto.setFull_name(
                patients.stream()
                        .map(p -> p.getFullName())
                        .toList()
        );



        dto.setPhone(
                patients.stream()
                        .map(Patient::getPhone)
                        .filter(Objects::nonNull)
                        .toList()
        );

        dto.setAddress(
                patients.stream()
                        .map(Patient::getAddress)
                        .filter(Objects::nonNull)
                        .toList()
        );

        return dto;
    }

    private Patient_AppointmentDto convertToDto(Patient patient) {
        Patient_AppointmentDto dto = new Patient_AppointmentDto();
        dto.setPatientId(patient.getId());
        dto.setFullName(patient.getFullName());
        return dto;
    }
    @Override
    public List<PatientCardDTO> getListPatients() {

        User user = getCurrentUser();

        Doctor doctor = doctorRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found")
                );
System.out.println(" doctor "+doctor.getName());

        // ======================================================
        // ACTIVE APPOINTMENTS
        // ======================================================

        List<Appointment> appointments =
                appointmentRepository.findTodayActiveAppointments(
                        doctor.getId()
                );

        // ======================================================
        // ✅ CURRENT CONSULTATION EXISTS
        // ======================================================

        if (!appointments.isEmpty()) {

            return appointments.stream()
                    .map(a -> {

                        PatientCardDTO dto =
                                mapAppointmentToCard(a);

                        dto.setActiveAppointment(true);

                        return dto;

                    })
                    .toList();
        }

        // ======================================================
        // ❌ NO ACTIVE CONSULTATION
        // ======================================================

        PatientCardDTO dto = new PatientCardDTO();

        dto.setActiveAppointment(false);

        Optional<Appointment> nextAppointment =
                appointmentRepository.findNextAppointment(
                        doctor.getId(),
                        LocalDateTime.now()
                );

        nextAppointment.ifPresent(a ->
                dto.setNextAppointmentTime(
                        a.getStartTime()
                )
        );

        return List.of(dto);
    }


    private PatientCardDTO mapAppointmentToCard(Appointment appointment) {

        PatientCardDTO dto = new PatientCardDTO();

        Patient patient = appointment.getPatient();

        if (patient != null) {

            dto.setPatientId(patient.getId());
            dto.setFull_name_patient(patient.getFullName());
        }

        dto.setAppointmentId(appointment.getId());

        dto.setAppointmentDate(
                appointment.getAppointmentDate()
        );

        if (appointment.getDoctor() != null) {

            dto.setDoctorName(
                    appointment.getDoctor().getName()
            );
        }

        return dto;
    }

}
