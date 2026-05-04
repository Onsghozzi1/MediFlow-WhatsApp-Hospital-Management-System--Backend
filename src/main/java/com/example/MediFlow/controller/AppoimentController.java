package com.example.MediFlow.controller;

import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentFilter;
import com.example.MediFlow.Dtos.ApoimentsDtos.ApoimentsResponse;
import com.example.MediFlow.Dtos.ApoimentsDtos.AppoimentsDto;
import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.services.IAppointmentService;
import com.example.MediFlow.utility.AppConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/Appointment/")
@Validated
@RestController
public class AppoimentController {
    @Autowired
    private IAppointmentService iAppointmentService ;

    @PostMapping("/add-appointment")
    public AppoimentsDto create(@Valid @RequestBody AppoimentsDto appointment) {
        return iAppointmentService.create(appointment);
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

}
