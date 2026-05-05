package com.example.MediFlow.controller;

import com.example.MediFlow.Dtos.ApiResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.Patients.PatientDTO;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.services.IAppointmentService;
import com.example.MediFlow.utility.AppConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/v1/Appointment/")
@Validated
@RestController
public class AppoimentController {
    @Autowired
    private IAppointmentService iAppointmentService ;

    @PostMapping("/add-appointment")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AppoimentsDto appointment) {

        iAppointmentService.create(appointment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Appointment created successfully","SUCESS",null));
    }
    @RequestMapping(value = "/AppointmentPagination", method = RequestMethod.POST)
    public ApoimentsResponse getAppointmentPagination(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestBody(required = false) ApoimentFilter filtre
    ) {
        return iAppointmentService.getAppointmentPagination(pageNo, pageSize, sortBy, sortDir,filtre);
    }
    @PutMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        iAppointmentService.changeDeleteStatus(id);
        return ResponseEntity.ok(
                new ApiResponse("Appointment deleted successfully", "deleted",null)
        );


    }
    @PutMapping("/edit-appointment/{id}")
    public ResponseEntity<ApiResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppoimentsDto dto) {
        iAppointmentService.update(id, dto);
        return ResponseEntity.ok(
                new ApiResponse(
                        "Appointment updated successfully",
                        "success",
                        null
                )
        );
    }
}
