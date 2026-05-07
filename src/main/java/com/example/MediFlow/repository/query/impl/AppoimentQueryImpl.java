package com.example.MediFlow.repository.query.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.Appoi_dto;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.mapper.UserMapper;
import com.example.MediFlow.repository.AppointmentRepository;
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
    public AppoimentQueryImpl(EntityManager em) {
        this.em = em;
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
    private Appoi_dto convertOneToDto( Appointment post) {
        Appoi_dto dto = new Appoi_dto();
        dto.setId(post.getId());
        dto.setStatus(post.getStatus());
        dto.setAppointmentDate(post.getAppointmentDate());
        dto.setPriority(post.getPriority());
        dto.setStatus(post.getStatus());
        dto.setAppointmentType(post.getAppointment_Type());
        // Check if patient exists to avoid NullPointerException
        if (post.getPatient() != null) {
            dto.setPatient_name(getPatient(post.getPatient().getId()));
            dto.setPatientId(post.getPatient().getId());
        } else {
            dto.setPatientId(null); // optional
        }
        // Avoid NullPointerException for doctor
        if (post.getDoctor() != null) {
            dto.setDoctor_name(getDoctor(post.getDoctor().getEmail()));
        }
        dto.setNotes(post.getNotes());
        dto.setReason(post.getReason());
        return dto;
    }

    // Retrieves patient's full name safely using Optional
    String getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .map(Patient::getFullName) // extract full name if found
                .orElse(null); // return null if patient not found
    }
    // Retrieves doctor's full name safely using Optional
    String getDoctor(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getFirstName() + " " + user.getLastName()) // combine names
                .orElse(null); // return null if doctor not found
    }

    private Map<String, Long> getAppointmentStats() {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // TOTAL
        CriteriaQuery<Long> totalQuery = cb.createQuery(Long.class);
        Root<Appointment> root = totalQuery.from(Appointment.class);

        totalQuery.select(cb.count(root))
                .where(cb.equal(root.get("is_delete"), false));

        Long total = em.createQuery(totalQuery).getSingleResult();
//
//        // TODAY
      CriteriaQuery<Long> todayQuery = cb.createQuery(Long.class);
        Root<Appointment> todayRoot = todayQuery.from(Appointment.class);
//
        todayQuery.select(cb.count(todayRoot))
                .where(cb.and(
                        cb.equal(todayRoot.get("is_delete"), false),
                        cb.equal(todayRoot.get("AppointmentDate"), LocalDate.now())
                ));
        Long today = em.createQuery(todayQuery).getSingleResult();
//
//        // COMPLETED (FIXED ENUM)
        CriteriaQuery<Long> completedQuery = cb.createQuery(Long.class);
       Root<Appointment> completedRoot = completedQuery.from(Appointment.class);

      completedQuery.select(cb.count(completedRoot))
               .where(cb.and(
                       cb.equal(completedRoot.get("is_delete"), false),
                       cb.equal(completedRoot.get("status"), Status.DONE)
             ));
//
      Long completed = em.createQuery(completedQuery).getSingleResult();
//
//        // UPCOMING
       CriteriaQuery<Long> upcomingQuery = cb.createQuery(Long.class);
        Root<Appointment> upcomingRoot = upcomingQuery.from(Appointment.class);
//
        upcomingQuery.select(cb.count(upcomingRoot))
                .where(cb.and(
                        cb.equal(upcomingRoot.get("is_delete"), false),

                        cb.greaterThan(
                                upcomingRoot.get("AppointmentDate"),
                                LocalDate.now()
                        ),

                        cb.or(
                                cb.equal(upcomingRoot.get("status"), Status.PENDING),
                                cb.equal(upcomingRoot.get("status"), Status.CONFIRMED)
                        )
                ));

        Long upcoming = em.createQuery(upcomingQuery).getSingleResult();
        Map<String, Long> stats = new HashMap<>();
        stats.put("total_Appointments", total);
     stats.put("today_Appointments", today);
        stats.put("completed", completed);
      stats.put("Upcoming", upcoming);

        return stats;
    }
}
