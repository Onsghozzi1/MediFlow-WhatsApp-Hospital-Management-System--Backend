package com.example.MediFlow.controller;

import com.example.MediFlow.Dtos.ApiResponse;
import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.Patients.PatientFilter;
import com.example.MediFlow.Dtos.Patients.PatientResponseDto;
import com.example.MediFlow.Dtos.Patients.Patient_AppointmentDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.entity.Patient;
import com.example.MediFlow.services.IPatientService;
import com.example.MediFlow.utility.AppConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/Patient/")
@Validated
@RestController
public class PatientController {
    @Autowired
    private IPatientService iPatientService ;

    // =========================
    // CREATE PATIENT
    // =========================
    @PostMapping("/add-patient")
    public ResponseEntity<Map<String, String>> createPatient(@Valid @RequestBody PatientDTO dto) {
        iPatientService.create_Patient(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient created successfully");
        return ResponseEntity.ok(response);
    }
    // =========================
    // PAGINATION + FILTER
    // =========================
    @RequestMapping(value = "/PatientPagination", method = RequestMethod.POST)
    public PatientResponseDto getProjectPagination(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestBody(required = false) PatientFilter filtre
    ) {
        return iPatientService.getPatientPagination(pageNo, pageSize, sortBy, sortDir,filtre);
    }
    // =========================
    // UPDATE PATIENT (NEW)
    // =========================
    @PutMapping("/edit-patient/{id}")
    public ResponseEntity<Map<String, String>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO dto) {
         System.out.println("idd data "+id+ " patient received "+dto);
        iPatientService.updatePatient(id, dto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        iPatientService.changeDeleteStatus(id);
        return ResponseEntity.ok(
                new ApiResponse("Business deleted successfully", "deleted",null)
        );
    }
    @GetMapping("/get_patients")
    public ResponseEntity<List<Patient_AppointmentDto>> getAllPatients(  @RequestParam(required = false) Long appointmentId) {
        List<Patient_AppointmentDto> patients = iPatientService.getAllPatients(appointmentId);
        return ResponseEntity.ok(patients);
    }
}
