package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.ApoimentsDtos.*;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.mapper.AppoimentMapper;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.repository.PatientRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IAppoimentQuery;
import com.example.MediFlow.services.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

}
