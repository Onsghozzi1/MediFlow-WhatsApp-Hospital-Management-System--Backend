package com.example.MediFlow.controller;

import com.example.MediFlow.Dtos.ApiResponse;
import com.example.MediFlow.Dtos.Api_Response;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.ApoimentsDtos.Appointment_PatientDTO;
import com.example.MediFlow.entity.Appointment;
import com.example.MediFlow.entity.enums.Status;
import com.example.MediFlow.repository.AppointmentRepository;
import com.example.MediFlow.services.IAppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patient_appointments/")
public class PatientAppointmentController {
    @Autowired
    private IAppointmentService iAppointmentService;

    @PostMapping("add_appointment_patient")

    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Appointment_PatientDTO a) {

        iAppointmentService.create_Appointment_patient(a);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Appointment created successfully","SUCESS",null));
    }
    @GetMapping("/booked-slots")
    public List<String> getBookedSlots(
            @RequestParam LocalDate date
    ) {
        return iAppointmentService.getBookedSlots(date);
    }
}
