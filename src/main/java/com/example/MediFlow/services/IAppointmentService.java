package com.example.MediFlow.services;

import com.example.MediFlow.Dtos.ApoimentsDtos.*;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IAppointmentService {
    AppoimentsDto create(AppoimentsDto appointmentDto);
    public ApoimentsResponse getAppointmentPagination(int pageNo, int pageSize, String sortBy, String sortDir, ApoimentFilter filter);
    public void changeDeleteStatus(Long id);
    public AppoimentsDto update(Long id, AppoimentsDto dto);
    List<Appointment_calendar>  getAllAppointments();

    List <AllPatients>   getAllAppointmentsPatient();
    public List<LocalTime> getAvailableSlots(
            Long doctorId,
            LocalDate date
    );
    Appointment moveAppointment(Long id, MoveAppointmentRequest request);
    Appointment_PatientDTO create_Appointment_patient(Appointment_PatientDTO appointmentPatientDTO);
    public List<String> getBookedSlots(LocalDate date);
}
