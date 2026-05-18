package com.example.MediFlow.repository.query.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.Appoi_dto;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.Doctor.DoctorDto;
import com.example.MediFlow.Dtos.Doctor.DoctorDtos_consultation;
import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.Dtos.consultation.*;
import com.example.MediFlow.entity.*;
import com.example.MediFlow.entity.enums.Roles;
import com.example.MediFlow.exception.UserServiceCustomException;
import com.example.MediFlow.mapper.AppoimentMapper;
import com.example.MediFlow.mapper.DoctorMapper;
import com.example.MediFlow.mapper.PatientMapper;
import com.example.MediFlow.mapper.UserMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IConsultationQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Controller
public class ConsultationQueryImpl implements IConsultationQuery {

    private final EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private AppoimentMapper appoimentMapper;

    public ConsultationQueryImpl(EntityManager em) {
        this.em = em;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceCustomException("User not found","error"));
    }
    // =========================
    // PAGINATION
    // =========================
    @Override
    public ConsultationResponse getConsultationPagination(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            ConsultationFilter filter) {

        ConsultationResponse response = new ConsultationResponse();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Consultation> cq = cb.createQuery(Consultation.class);
        Root<Consultation> root = cq.from(Consultation.class);

        cq.select(root).distinct(true);

        Predicate[] predicates = getPredicates(filter, cb, root, cq);
        cq.where(predicates);

        cq.orderBy(cb.desc(root.get(sortBy)));

        long count = countUserPagination(filter);

        TypedQuery<Consultation> query = em.createQuery(cq);
        query.setFirstResult(pageNo * pageSize);
        query.setMaxResults(pageSize);

        List<Consultation> list = query.getResultList();

        List<ConsultationDTO> dtos = list.stream()
                .map(this::convertOneToDto)
                .collect(Collectors.toList());

        response.setContent(dtos);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements(count);
        fillStatistics(response);

        return response;
    }

    // =========================
    // COUNT
    // =========================
    public long countUserPagination(ConsultationFilter filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Consultation> root = cq.from(Consultation.class);

        cq.select(cb.countDistinct(root));

        Predicate[] predicates = getPredicates(filter, cb, root, cq);
        cq.where(predicates);

        return em.createQuery(cq).getSingleResult();
    }
    // =========================
    // FILTER (ADMIN SAFE FIX)
    // =========================
    private <T> Predicate[] getPredicates(
            ConsultationFilter filter,
            CriteriaBuilder cb,
            Root<Consultation> root,
            CriteriaQuery<T> cq) {

        List<Predicate> predicates = new ArrayList<>();

        User user = getCurrentUser();
        boolean isAdmin = user.getRoleTypes() == Roles.ADMIN;

        // if DOCTOR → filter by doctor
        if (!isAdmin) {

            Doctor doctor = user.getDoctor();
            if (doctor == null) {
                throw new RuntimeException("Doctor not found for user");
            }

            predicates.add(
                    cb.equal(root.get("doctor").get("id"), doctor.getId())
            );
        }
        if (filter.getId() != null && filter.getId().longValue() > 0) {
            predicates.add(cb.equal(root.get("id"), filter.getId()));
        }
        return predicates.toArray(new Predicate[0]);
    }

    // =========================
    // DTO MAPPING
    // =========================
    private ConsultationDTO convertOneToDto(Consultation consultation) {

        if (consultation == null) {
            return null;
        }

        ConsultationDTO dto = new ConsultationDTO();

        // DOCTOR
        dto.setDoctor(
                consultation.getDoctor() != null
                        ? ConvertDoctor(consultation.getDoctor())
                        : null
        );

        // PATIENT
        dto.setPatient(
                consultation.getPatient() != null
                        ? ConvertPatient(consultation.getPatient())
                        : null
        );

        // APPOINTMENT
        dto.setAppointment(
                consultation.getAppointment() != null
                        ? ConvertAppointment(consultation.getAppointment())
                        : null
        );
        dto.setDto(

                consultation.getPrescriptions()

                        .stream()

                        .map(p -> {

                            PrescriptionDTO prescriptionDTO =
                                    new PrescriptionDTO();

                            prescriptionDTO.setId(
                                    p.getId()
                            );

                            prescriptionDTO.setMedicineName(
                                    p.getMedicineName()
                            );

                            prescriptionDTO.setDosage(
                                    p.getDosage()
                            );

                            prescriptionDTO.setFrequency(
                                    p.getFrequency()
                            );

                            prescriptionDTO.setDuration(
                                    p.getDuration()
                            );

                            prescriptionDTO.setInstructions(
                                    p.getInstructions()
                            );

                            return prescriptionDTO;
                        })

                        .toList()
        );        // SIMPLE FIELDS
        dto.setReason(consultation.getReason());
        dto.setSymptoms(consultation.getSymptoms());
        dto.setDiagnosis(consultation.getDiagnosis());
        dto.setNotes(consultation.getNotes());
         dto.setStatus(consultation.getStatus());
         dto.setId(consultation.getId());
        return dto;
    }

    // =========================
    // DOCTOR MAPPER
    // =========================
    private DoctorDtos_consultation ConvertDoctor(Doctor doctor) {

        if (doctor == null) {
            return null;
        }

        DoctorDtos_consultation dto = new DoctorDtos_consultation();

        dto.setName(doctor.getName());

        return dto;
    }

    // =========================
    // PATIENT MAPPER
    // =========================
    private Patient_consultation_Dtos ConvertPatient(Patient patient) {

        if (patient == null) {
            return null;
        }

        Patient_consultation_Dtos dto = new Patient_consultation_Dtos();
        dto.setFullName(patient.getFullName());
        dto.setWhatsappNumber(patient.getWhatsappNumber());
        dto.setMedical_Record_ID(patient.getMedical_Record_ID());
        dto.setGender(patient.getGender());
        dto.setPhone(patient.getPhone());
        dto.setBirthDate(patient.getBirthDate());
        dto.setAddress(patient.getAddress());
        return dto;
    }

    // =========================
    // APPOINTMENT MAPPER
    // =========================
    private Appoi_dto ConvertAppointment(Appointment appointment) {

        if (appointment == null) {
            return null;
        }
        Appoi_dto dto = new Appoi_dto();
        dto.setId(appointment.getId());
        dto.setAppointmentDate(appointment.getAppointmentDate());

        return dto;
    }

    // =========================
    // STATS (ADMIN SAFE FIX)
    // =========================
    private void fillStatistics(ConsultationResponse response) {

        User user = getCurrentUser();
        boolean isAdmin = user.getRoleTypes() == Roles.ADMIN;

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> totalQ = cb.createQuery(Long.class);
        Root<Consultation> root = totalQ.from(Consultation.class);

        List<Predicate> pred = new ArrayList<>();

        if (!isAdmin) {
            pred.add(cb.equal(root.get("doctor").get("id"), user.getDoctor().getId()));
        }

        totalQ.select(cb.count(root)).where(pred.toArray(new Predicate[0]));

        Long total = em.createQuery(totalQ).getSingleResult();

        response.setTotal_Consultation(total);
        response.setToday_Consultation(0L);
        response.setCompleted(0L);
        response.setUpcoming(0L);
    }
}