package com.example.MediFlow.repository.query.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.Appoi_dto;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.Doctor.DoctorDto;
import com.example.MediFlow.Dtos.Doctor.DoctorDtos_consultation;
import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.Dtos.consultation.ConsultationDTO;
import com.example.MediFlow.Dtos.consultation.ConsultationFilter;
import com.example.MediFlow.Dtos.consultation.ConsultationResponse;
import com.example.MediFlow.Dtos.consultation.PrescriptionDTO;
import com.example.MediFlow.entity.*;
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
    // FILTERS (EMPTY FOR NOW)
    // =========================
    private <T> Predicate[] getPredicates(
            ConsultationFilter filter,
            CriteriaBuilder cb,
            Root<Consultation> root,
            CriteriaQuery<T> cq) {

        List<Predicate> predicates = new ArrayList<>();
        String email =
                getCurrentUser().getEmail();

        Join<Consultation, Doctor> doctorJoin =
                root.join("doctor");

        Join<Doctor, User> userJoin =
                doctorJoin.join("user");

        predicates.add(

                cb.equal(
                        userJoin.get("email"),
                        email
                )
        );

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
    private PatientDTO ConvertPatient(Patient patient) {

        if (patient == null) {
            return null;
        }

        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFullName(patient.getFullName());
        dto.setWhatsappNumber(patient.getWhatsappNumber());

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
    private void fillStatistics(
            ConsultationResponse response
    ) {

        String email =getCurrentUser().getEmail();

        CriteriaBuilder cb =
                em.getCriteriaBuilder();

        CriteriaQuery<Object[]> cq =
                cb.createQuery(Object[].class);

        Root<Consultation> root =
                cq.from(Consultation.class);

        // JOIN DOCTOR
        Join<Consultation, Doctor> doctorJoin =
                root.join("doctor");

        // JOIN USER
        Join<Doctor, User> userJoin =
                doctorJoin.join("user");

        // =========================
        // FILTER CURRENT USER
        // =========================
        Predicate userPredicate =

                cb.equal(
                        userJoin.get("email"),
                        email
                );

        // =========================
        // TOTAL
        // =========================
        Expression<Long> totalCount =
                cb.count(root);

        // =========================
        // COMPLETED
        // =========================
        Expression<Long> completedCount =
                cb.sum(

                        cb.<Long>selectCase()

                                .when(

                                        cb.equal(
                                                root.get("status"),
                                                "COMPLETED"
                                        ),

                                        1L
                                )

                                .otherwise(0L)
                );

        // =========================
        // UPCOMING
        // =========================
        Expression<Long> upcomingCount =
                cb.sum(

                        cb.<Long>selectCase()

                                .when(

                                        cb.equal(
                                                root.get("status"),
                                                "UPCOMING"
                                        ),

                                        1L
                                )

                                .otherwise(0L)
                );

        // =========================
        // TODAY
        // =========================
        Expression<Long> todayCount =
                cb.sum(

                        cb.<Long>selectCase()

                                .when(

                                        cb.equal(

                                                cb.function(
                                                        "DATE",
                                                        java.sql.Date.class,
                                                        root.get("createdAt")
                                                ),

                                                java.time.LocalDate.now()
                                        ),

                                        1L
                                )

                                .otherwise(0L)
                );

        // =========================
        // SELECT
        // =========================
        cq.multiselect(

                totalCount,
                todayCount,
                completedCount,
                upcomingCount
        );

        // =========================
        // WHERE
        // =========================
        cq.where(userPredicate);

        Object[] result =

                em.createQuery(cq)
                        .getSingleResult();

        // =========================
        // RESPONSE
        // =========================
        response.setTotal_Consultation(

                result[0] != null
                        ? (Long) result[0]
                        : 0L
        );

        response.setToday_Consultation(

                result[1] != null
                        ? (Long) result[1]
                        : 0L
        );

        response.setCompleted(

                result[2] != null
                        ? (Long) result[2]
                        : 0L
        );

        response.setUpcoming(

                result[3] != null
                        ? (Long) result[3]
                        : 0L
        );
    }
}