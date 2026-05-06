package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.exception.PatientAlreadyExistsException;
import com.example.MediFlow.mapper.PatientMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.PatientRepository;
import com.example.MediFlow.repository.query.IPatientQuery;
import com.example.MediFlow.services.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;

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


    private static final String PREFIX = "MR-";
    private static final SecureRandom random = new SecureRandom();


    @Override
    public PatientDTO create_Patient(PatientDTO patientDTO) {
        // ✅ Check if patient already exists
        boolean exists = patientRepository
                .existsByFullNameAndPhone(patientDTO.getFullName(), patientDTO.getPhone());
        if (exists) {
            throw new PatientAlreadyExistsException("Patient already exists with same full name and phone");
        }
        // Convert DTO to Entity
        Patient entity = patientMapper.mapToPatient(patientDTO);
        entity.setCreate_date_time(LocalDateTime.now());
        entity.setMedical_Record_ID(generateMedicalRecordId());
        entity.setAge(Period.between(patientDTO.getBirthDate(), LocalDate.now()).getYears());
        entity.setAddress(patientDTO.getAddress());
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

        // update timestamp
        patient.setUpdate_date_time(LocalDateTime.now());

        // update fields FROM DTO (IMPORTANT FIX)
        patient.setFullName(patientDTO.getFullName());
        patient.setPhone(patientDTO.getPhone());
        patient.setWhatsappNumber(patientDTO.getWhatsappNumber());
        patient.setAddress(patientDTO.getAddress());
        patient.setGender(patientDTO.getGender());
        patient.setBirthDate(patientDTO.getBirthDate());

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

        List<Patient> patients = patientRepository.findByIsDeleteFalse();

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

    private Patient_AppointmentDto convertToDto(Patient patient) {
        Patient_AppointmentDto dto = new Patient_AppointmentDto();
        dto.setPatientId(patient.getId());
        dto.setFullName(patient.getFullName());
        return dto;
    }


}
