package com.example.MediFlow.repository.query.impl;

import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.Dtos.user_dto.UserDTO;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.mapper.UserMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IPatientQuery;
import com.example.MediFlow.repository.query.IUserQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class PatientQueryImpl implements IPatientQuery {
    private final EntityManager em;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AppointmentRepository appointmentRepository;

    public PatientQueryImpl(EntityManager em) {
        this.em = em;
    }
    @Override
    public PatientResponseDto getPatientPagination(int pageNo, int pageSize, String sortBy, String sortDir, PatientFilter filter) {
        PatientResponseDto patientResponseDto = new PatientResponseDto();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Patient> cq = cb.createQuery(Patient.class);
        Root<Patient> root = cq.from(Patient.class);

        Predicate[] predicatesArray = getPredicates(filter, cb, root, cq);
        cq.distinct(true);
        cq.where(predicatesArray);
        cq.orderBy(cb.desc(root.get(sortBy)));

        long count = countUserPagination(filter);
        long totalMale = countByGender("MALE", filter);
        long totalFemale = countByGender("FEMALE", filter);

        TypedQuery<Patient> query = em.createQuery(cq);
        query.setFirstResult(pageNo * pageSize);
        query.setMaxResults(pageSize);

        List<Patient> porteFeuille = query.getResultList();

        Set<Long> assignedPatientIds = appointmentRepository.findalldPatientIds();

        List<PatientDTO> patientDTO = porteFeuille.stream()
                .map(p -> convertOneToDto(p, assignedPatientIds))
                .collect(Collectors.toList());

        patientResponseDto.setContent(patientDTO);
        patientResponseDto.setPageNo(pageNo);
        patientResponseDto.setTotalElements(count);
        patientResponseDto.setPageSize(pageSize);
        patientResponseDto.setTotalMale(totalMale);
        patientResponseDto.setTotalFemale(totalFemale);
        return patientResponseDto;
    }

    public long countUserPagination(PatientFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Patient> root = cq.from(Patient.class);
        Predicate[] predicatesArray = getPredicates(filter, cb, root, cq);
        cq.select(cb.countDistinct(root));
        cq.where(predicatesArray);
        return em.createQuery(cq).getSingleResult();
    }
    private <T> Predicate[] getPredicates(
            PatientFilter filter,
            CriteriaBuilder cb,
            Root<Patient> root,
            CriteriaQuery<T> cq
    ) {

        List<Predicate> predicates = new ArrayList<>();

        // =========================
        // NOT DELETED
        // =========================
        predicates.add(cb.equal(root.get("isDelete"), false));

        // =========================
        // FILTER BY ID
        // =========================
        if (filter.getId() != null && filter.getId() > 0) {

            predicates.add(
                    cb.equal(root.get("id"), filter.getId())
            );
        }

        // =========================
        // FILTER BY MEDICAL RECORD ID
        // =========================
        if (filter.getMedicalRecordIds() != null
                && !filter.getMedicalRecordIds().trim().isEmpty()) {

            predicates.add(
                    cb.like(
                            cb.lower(root.get("medical_Record_ID")),
                            "%" + filter.getMedicalRecordIds().toLowerCase() + "%"
                    )
            );
        }
// =========================
// FILTER BY GENDER
// =========================
        if (filter.getGender() != null) {

            predicates.add(
                    cb.equal(
                            root.get("gender"),
                            filter.getGender()
                    )
            );
        }
        // =========================
        // FILTER BY FULL NAME
        // =========================
            if (filter.getFull_name() != null
                    && !filter.getFull_name().trim().isEmpty()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("fullName")),
                                "%" + filter.getFull_name().toLowerCase() + "%"
                        )
                );
            }
        // =========================
        // FILTER BY PHONE
        // =========================
        if (filter.getPhone() != null
                && !filter.getPhone().trim().isEmpty()) {

            predicates.add(
                    cb.like(
                            cb.lower(root.get("phone")),
                            "%" + filter.getPhone().toLowerCase() + "%"
                    )
            );
        }

        // =========================
        // FILTER BY ADDRESS
        // =========================
        if (filter.getAddress() != null
                && !filter.getAddress().trim().isEmpty()) {

            predicates.add(
                    cb.like(
                            cb.lower(root.get("address")),
                            "%" + filter.getAddress().toLowerCase() + "%"
                    )
            );
        }

        return predicates.toArray(new Predicate[0]);
    }
    private long countByGender(String gender, PatientFilter filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Patient> root = cq.from(Patient.class);

        List<Predicate> predicates = new ArrayList<>();

        // garder les filtres existants
        Predicate[] basePredicates = getPredicates(filter, cb, root, cq);
        for (Predicate p : basePredicates) {
            predicates.add(p);
        }

        // ajouter filtre gender
        predicates.add(cb.equal(root.get("gender"), gender));

        cq.select(cb.count(root));
        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }
    private PatientDTO convertOneToDto(Patient patient, Set<Long> assignedIds) {

        PatientDTO dto = new PatientDTO();

        dto.setId(patient.getId());

        // 🔥 هنا السحر
        if (assignedIds.contains(patient.getId())) {
            dto.setFullName(patient.getFullName());
            dto.setPatient_activated(false);
        } else {
            dto.setFullName(patient.getFullName() + " (already assigned)");
            dto.setPatient_activated(true);

        }

        dto.setPhone(patient.getPhone());
        dto.setMedicalHistory(patient.getMedicalHistory());
        dto.setBirthDate(patient.getBirthDate());
        dto.setWhatsappNumber(patient.getWhatsappNumber());
        dto.setGender(patient.getGender());
        dto.setAddress(patient.getAddress());
        dto.setAge(patient.getAge());
        dto.setMedical_Record_ID(patient.getMedical_Record_ID());

        return dto;
    }


}
