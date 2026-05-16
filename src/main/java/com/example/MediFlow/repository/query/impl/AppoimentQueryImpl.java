package com.example.MediFlow.repository.query.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.Appoi_dto;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Doctor;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.entity.enums.Roles;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.exception.UserServiceCustomException;
import com.example.MediFlow.mapper.UserMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.DoctorRepository;
import com.example.MediFlow.repository.PatientRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IAppoimentQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AppoimentQueryImpl implements IAppoimentQuery {
    private final EntityManager em;

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    public AppoimentQueryImpl(EntityManager em) {
        this.em = em;
    }
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceCustomException("User not found","error"));
    }


    @Override
    public ApoimentsResponse getAppointmentPagination(int pageNo, int pageSize, String sortBy, String sortDir, ApoimentFilter filter) {
        ApoimentsResponse apoimentsResponse = new ApoimentsResponse();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery< Appointment> cq = cb.createQuery( Appointment.class);
        Root< Appointment> root = cq.from( Appointment.class);

        Predicate[] predicatesArray = getPredicates(filter, cb, root, cq);
        cq.distinct(true);
        cq.where(predicatesArray);
        cq.orderBy(cb.desc(root.get(sortBy)));

        long count = countUserPagination(filter);

        TypedQuery< Appointment> query = em.createQuery(cq);
        query.setFirstResult(pageNo * pageSize);
        query.setMaxResults(pageSize);
        List< Appointment> porteFeuille = query.getResultList();
        List<Appoi_dto> appoimentsDto = porteFeuille.stream()
                .map(this::convertOneToDto)
                .collect(Collectors.toList());

        // ✅ CALL STATS HERE (FIX)
        Map<String, Long> stats = getAppointmentStats();

        apoimentsResponse.setContent(appoimentsDto);
        apoimentsResponse.setPageNo(pageNo);
        apoimentsResponse.setTotalElements(count);
        apoimentsResponse.setPageSize(pageSize);
        apoimentsResponse.setTotal_Appointments(stats.get("total_Appointments"));
      apoimentsResponse.setToday_Appointments(stats.get("today_Appointments"));
 apoimentsResponse.setCompleted(stats.get("completed"));
apoimentsResponse.setUpcoming(stats.get("Upcoming"));

  return apoimentsResponse;
    }

    public long countUserPagination(ApoimentFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Appointment> root = cq.from( Appointment.class);
        Predicate[] predicatesArray = getPredicates(filter, cb, root, cq);
        cq.select(cb.countDistinct(root));
        cq.where(predicatesArray);
        return em.createQuery(cq).getSingleResult();
    }

    private <T> Predicate[] getPredicates(ApoimentFilter filter, CriteriaBuilder cb, Root< Appointment> root, CriteriaQuery<T> cq) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("is_delete"), false));

        if (filter.getId() != null && filter.getId().longValue() > 0) {
            predicates.add(cb.equal(root.get("id"), filter.getId()));
        }
        User user = getCurrentUser();
        boolean isAdmin =
                user.getRoleTypes() == Roles.ADMIN;
        if (!isAdmin) {
            predicates.add(
                    cb.equal(
                            root.get("doctor").get("id"),
                            user.getDoctor().getId()
                    )
            );
        }
        if (filter.getPatient_name() != null
                && !filter.getPatient_name().trim().isEmpty()) {
            predicates.add(
                    cb.like(
                            cb.lower(root.get("patient").get("fullName")),
                            "%" + filter.getPatient_name().toLowerCase() + "%"
                    )
            );
        }
        if (filter.getAppointmentDate() != null) {
            LocalDateTime selectedDateTime = filter.getAppointmentDate();
            LocalDate date = selectedDateTime.toLocalDate();
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            predicates.add(
                    cb.between(
                            root.get("AppointmentDate"),
                            startOfDay,
                            endOfDay
                    )
            );
        }
        if (filter.getPriority() != null ) {
            predicates.add(cb.equal(root.get("priority"), filter.getPriority()));
        }
        if (filter.getStatus() != null ) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }
        if (filter.getAppointment_Type() != null ) {
            predicates.add(cb.equal(root.get("appointment_Type"), filter.getAppointment_Type()));
        }

        return predicates.toArray(new Predicate[0]);
    }
    private Appoi_dto convertOneToDto(Appointment post) {

        Appoi_dto dto = new Appoi_dto();

        dto.setId(post.getId());

        dto.setStatus(post.getStatus());

        dto.setAppointmentDate(post.getAppointmentDate());

        dto.setPriority(post.getPriority());

        dto.setAppointmentType(post.getAppointment_Type());

        dto.setNotes(post.getNotes());

        dto.setReason(post.getReason());

        // =========================
        // PATIENT
        // =========================

        if (post.getPatient() != null) {

            dto.setPatient_name(
                    post.getPatient().getFullName()
            );

            dto.setPatientId(
                    post.getPatient().getId()
            );

            dto.setWhatsApp_number(
                    post.getPatient().getWhatsappNumber()
            );
        }

        // =========================
        // DOCTOR
        // =========================

        if (post.getDoctor() != null) {

            dto.setDoctor_name(
                    post.getDoctor().getName()
            );
        }

        return dto;
    }

    // Retrieves patient's full name safely using Optional
    String getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .map(Patient::getFullName) // extract full name if found
                .orElse(null); // return null if patient not found
    }
    // Retrieves doctor's full name safely using Optional
    public String getDoctor(String email) {

        return userRepository.findByEmail(email)
                .flatMap(user -> doctorRepository.findByUserId(user.getId()))
                .map(Doctor::getName)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    private Map<String, Long> getAppointmentStats() {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        User user = getCurrentUser();

        boolean isAdmin =
                Roles.ADMIN.equals(user.getRoleTypes());

        Doctor doctor = null;

        if (!isAdmin) {

            doctor = doctorRepository
                    .findByUserId(user.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Doctor not found")
                    );
        }

        Map<String, Long> stats = new HashMap<>();

        // ======================
        // TOTAL
        // ======================

        CriteriaQuery<Long> totalQuery =
                cb.createQuery(Long.class);

        Root<Appointment> totalRoot =
                totalQuery.from(Appointment.class);

        List<Predicate> totalPredicates =
                new ArrayList<>();

        totalPredicates.add(
                cb.equal(
                        totalRoot.get("is_delete"),
                        false
                )
        );

        if (!isAdmin) {

            totalPredicates.add(
                    cb.equal(
                            totalRoot.get("doctor").get("id"),
                            doctor.getId()
                    )
            );
        }

        totalQuery.select(cb.count(totalRoot));

        totalQuery.where(
                totalPredicates.toArray(new Predicate[0])
        );

        Long total =
                em.createQuery(totalQuery)
                        .getSingleResult();

        // ======================
        // TODAY
        // ======================

        CriteriaQuery<Long> todayQuery =
                cb.createQuery(Long.class);

        Root<Appointment> todayRoot =
                todayQuery.from(Appointment.class);

        List<Predicate> todayPredicates =
                new ArrayList<>();

        todayPredicates.add(
                cb.equal(
                        todayRoot.get("is_delete"),
                        false
                )
        );

        if (!isAdmin) {

            todayPredicates.add(
                    cb.equal(
                            todayRoot.get("doctor").get("id"),
                            doctor.getId()
                    )
            );
        }

        todayPredicates.add(
                cb.equal(
                        todayRoot.get("AppointmentDate"),
                        LocalDate.now()
                )
        );

        todayQuery.select(cb.count(todayRoot));

        todayQuery.where(
                todayPredicates.toArray(new Predicate[0])
        );

        Long today =
                em.createQuery(todayQuery)
                        .getSingleResult();

        // ======================
        // COMPLETED
        // ======================

        CriteriaQuery<Long> completedQuery =
                cb.createQuery(Long.class);

        Root<Appointment> completedRoot =
                completedQuery.from(Appointment.class);

        List<Predicate> completedPredicates =
                new ArrayList<>();

        completedPredicates.add(
                cb.equal(
                        completedRoot.get("is_delete"),
                        false
                )
        );

        if (!isAdmin) {

            completedPredicates.add(
                    cb.equal(
                            completedRoot.get("doctor").get("id"),
                            doctor.getId()
                    )
            );
        }

        completedPredicates.add(
                completedRoot.get("status")
                        .in(Status.DONE, Status.COMPLETED)
        );

        completedQuery.select(cb.count(completedRoot));

        completedQuery.where(
                completedPredicates.toArray(new Predicate[0])
        );

        Long completed =
                em.createQuery(completedQuery)
                        .getSingleResult();

        // ======================
        // UPCOMING
        // ======================

        CriteriaQuery<Long> upcomingQuery =
                cb.createQuery(Long.class);

        Root<Appointment> upcomingRoot =
                upcomingQuery.from(Appointment.class);

        List<Predicate> upcomingPredicates =
                new ArrayList<>();

        upcomingPredicates.add(
                cb.equal(
                        upcomingRoot.get("is_delete"),
                        false
                )
        );

        if (!isAdmin) {

            upcomingPredicates.add(
                    cb.equal(
                            upcomingRoot.get("doctor").get("id"),
                            doctor.getId()
                    )
            );
        }

        upcomingPredicates.add(
                cb.greaterThan(
                        upcomingRoot.get("AppointmentDate"),
                        LocalDate.now()
                )
        );

        upcomingPredicates.add(
                upcomingRoot.get("status").in(
                        Status.PENDING,
                        Status.CONFIRMED,
                        Status.SCHEDULED
                )
        );

        upcomingQuery.select(cb.count(upcomingRoot));

        upcomingQuery.where(
                upcomingPredicates.toArray(new Predicate[0])
        );

        Long upcoming =
                em.createQuery(upcomingQuery)
                        .getSingleResult();

        // ======================
        // RESULT
        // ======================

        stats.put("total_Appointments", total);
        stats.put("today_Appointments", today);
        stats.put("completed", completed);
        stats.put("Upcoming", upcoming);

        return stats;
    }


}
